# Iteration 04: AWS Infrastructure with CDK

## Context

The Spring Boot API (iterations 01-03) runs locally with Docker Compose PostgreSQL. This iteration sets up AWS infrastructure to deploy it on ECS Fargate behind an ALB with an RDS PostgreSQL database, using TypeScript CDK v2. The domain `strava.codinario.com` (registered at Websupport) will point to the ALB via HTTPS.

## Architecture Overview

```
Internet → strava.codinario.com (CNAME at Websupport)
         → ALB (HTTPS:443, ACM cert)
         → ECS Fargate Service (port 8080)
         → RDS PostgreSQL (port 5432, private)
         → CloudWatch Logs
```

All resources live in the **default VPC**. One CDK stack per environment (dev/uat/prod) with different sizing.

## Project Structure

```
infra/
├── bin/
│   └── app.ts                # CDK app entry point - instantiates stacks per env
├── lib/
│   ├── config.ts             # Per-environment configuration (instance sizes, scaling)
│   └── strava-stack.ts       # Main stack: VPC lookup, RDS, ECS, ALB, ACM, CloudWatch
├── cdk.json
├── package.json
├── tsconfig.json
└── README.md                 # Setup & deploy instructions

Dockerfile                    # Multi-stage: Gradle build → JRE 21 runtime (project root)
```

## Environment Configuration (`lib/config.ts`)

```typescript
interface EnvironmentConfig {
  account: string;
  region: string;
  rdsInstanceClass: string;       // "db.t4g.micro" | "db.t4g.small" | ...
  fargateTaskCpu: number;         // 256 | 512 | 1024
  fargateTaskMemoryMiB: number;   // 512 | 1024 | 2048
  desiredTaskCount: number;       // 1 | 2 | ...
  domainName: string;             // "strava-dev.codinario.com" | "strava.codinario.com"
  dbName: string;                 // "lab_strava_dev_db" | "lab_strava_uat_db" | ...
}
```

| Setting | dev | uat | prod |
|---------|-----|-----|------|
| RDS instance | db.t4g.micro | db.t4g.small | db.t4g.medium |
| Fargate CPU/Mem | 256/512 | 512/1024 | 1024/2048 |
| Task count | 1 | 1 | 2 |
| Domain | strava-dev.codinario.com | strava-uat.codinario.com | strava.codinario.com |
| DB name | lab_strava_dev_db | lab_strava_uat_db | lab_strava_prod_db |

## Stack Resources (`lib/strava-stack.ts`)

### 1. Default VPC Lookup
- `ec2.Vpc.fromLookup()` to reference the default VPC

### 2. Security Groups
- **ALB SG**: Inbound 80/443 from `0.0.0.0/0`
- **ECS SG**: Inbound 8080 from ALB SG only
- **RDS SG**: Inbound 5432 from ECS SG only

### 3. RDS PostgreSQL
- Engine: PostgreSQL 15
- Instance class: from env config (db.t4g.micro for dev)
- Credentials: auto-generated via Secrets Manager (`rds.DatabaseSecret`)
- Database name: env-specific (e.g., `lab_strava_dev_db`)
- No public access, single-AZ for dev
- Deletion protection off for dev, on for prod

### 4. ACM Certificate
- `Certificate` with `CertificateValidation.fromDns()` (no Route53 hosted zone)
- Domain: from env config (e.g., `strava-dev.codinario.com`)
- **Manual step**: CDK deploy will pause until DNS validation CNAME is added at Websupport

### 5. ECS Cluster + Fargate Service (using `ApplicationLoadBalancedFargateService`)
- Higher-level construct that creates ALB + Service + Task Definition + Target Group
- Container image: `ContainerImage.fromAsset('../')` (builds Dockerfile from project root)
- Container port: 8080
- Health check: `/api/v1/users` (GET, existing endpoint)
- Environment variables passed to container:
  - `SPRING_PROFILES_ACTIVE=dev`
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://<rds-endpoint>:5432/lab_strava_{env}_db`
  - `SERVER_PORT=8080`
- Secrets from Secrets Manager:
  - `SPRING_DATASOURCE_USERNAME` → RDS secret `username` field
  - `SPRING_DATASOURCE_PASSWORD` → RDS secret `password` field
- Certificate attached for HTTPS on ALB
- HTTP → HTTPS redirect

### 6. IAM Roles
- **ECS Task Execution Role**: Used by the ECS agent to:
  - Pull container images from ECR
  - Write logs to CloudWatch
  - Read RDS credentials from Secrets Manager
  - Based on `AmazonECSTaskExecutionRolePolicy` + Secrets Manager read policy
- **ECS Task Role**: Used by the application at runtime (empty for now, extensible for future AWS SDK calls like S3, SQS, etc.)

Note: `ApplicationLoadBalancedFargateService` creates these roles automatically, but we'll configure them explicitly to grant Secrets Manager access for RDS credentials.

### 7. CloudWatch Log Group
- Log group: `/ecs/lab-strava-{env}`
- Retention: 30 days for dev, 90 days for prod
- Created explicitly and referenced by the Fargate task

## Dockerfile (project root)

Multi-stage build:
```
Stage 1 (build): gradle:8-jdk21 → ./gradlew bootJar
Stage 2 (runtime): eclipse-temurin:21-jre-alpine → copy jar, expose 8080, run
```

## Spring Boot Config Changes

**None needed.** Spring Boot natively maps environment variables to properties:
- `SPRING_DATASOURCE_URL` → `spring.datasource.url`
- `SPRING_DATASOURCE_USERNAME` → `spring.datasource.username`
- `SPRING_DATASOURCE_PASSWORD` → `spring.datasource.password`

Liquibase will run on startup using the same datasource, migrating the RDS database automatically.

## Implementation Order

1. **Create Dockerfile** at project root (multi-stage Gradle + JRE 21)
2. **Initialize CDK project** in `infra/` (`cdk init app --language typescript`)
3. **Create `lib/config.ts`** with per-environment configuration
4. **Create `lib/strava-stack.ts`** with all resources:
   - VPC lookup → Security Groups → RDS → ACM Certificate → ECS/ALB → CloudWatch
5. **Update `bin/app.ts`** to instantiate stack per environment
6. **Create `infra/README.md`** with deployment instructions
7. **Add `.dockerignore`** to exclude unnecessary files from Docker build

## CDK Stack Outputs

The stack will output:
- ALB DNS name (for CNAME setup at Websupport)
- RDS endpoint
- ACM certificate validation CNAME records (for DNS validation at Websupport)
- ECS cluster name
- CloudWatch log group name

## DNS Setup (Manual Steps)

1. During `cdk deploy`, ACM creates a certificate → note the CNAME validation record
2. Add CNAME at Websupport: `_acme-challenge.strava-dev.codinario.com` → ACM validation value
3. Wait for certificate validation (can take 5-30 minutes)
4. After deploy completes, add CNAME at Websupport: `strava-dev.codinario.com` → ALB DNS name

## Deployment Commands

```bash
cd infra
npm install
npx cdk bootstrap              # First time only
npx cdk deploy LabStrava-dev    # Deploy dev environment
npx cdk deploy LabStrava-uat    # Deploy uat environment
npx cdk diff LabStrava-dev      # Preview changes
npx cdk destroy LabStrava-dev   # Tear down
```

## Verification

1. `cd infra && npm install && npx cdk synth` — synthesizes CloudFormation without deploying
2. `docker build -t lab-strava .` — verify Dockerfile builds successfully
3. `npx cdk deploy LabStrava-dev` — deploy to AWS (requires AWS credentials + manual DNS validation)
4. After deploy: `curl https://strava-dev.codinario.com/api/v1/users` — verify API responds
5. Check CloudWatch logs for application startup and Liquibase migration output
