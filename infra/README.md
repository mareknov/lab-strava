# Lab Strava - AWS Infrastructure

CDK v2 (TypeScript) project for deploying the Spring Boot API on AWS ECS Fargate.

## Architecture

```
Internet → strava.codinario.com (CNAME at Websupport)
         → ALB (HTTPS:443, ACM cert)
         → ECS Fargate Service (port 8080)
         → RDS PostgreSQL (port 5432, private)
         → CloudWatch Logs
```

## Prerequisites

- Node.js 18+
- AWS CLI configured with credentials
- Docker (for building container images)

## Setup

```bash
cd infra
npm install
npx cdk bootstrap   # First time only, per account/region
```

## Deploy

```bash
npx cdk deploy LabStrava-dev    # Deploy dev
npx cdk deploy LabStrava-uat    # Deploy uat
npx cdk deploy LabStrava-prod   # Deploy prod
```

## Other Commands

```bash
npx cdk synth LabStrava-dev     # Synthesize CloudFormation template
npx cdk diff LabStrava-dev      # Preview changes
npx cdk destroy LabStrava-dev   # Tear down environment
```

## DNS Setup (Manual Steps at Websupport)

1. Run `npx cdk deploy LabStrava-dev`
2. CDK will pause waiting for ACM certificate validation
3. Go to AWS Console → Certificate Manager → find the pending certificate
4. Copy the CNAME validation record (name and value)
5. Add CNAME at Websupport: `_acme-challenge.strava-dev.codinario.com` → validation value
6. Wait for certificate validation (5-30 minutes)
7. After deploy completes, copy the ALB DNS name from stack outputs
8. Add CNAME at Websupport: `strava-dev.codinario.com` → ALB DNS name

## Environment Sizing

| Setting | dev | uat | prod |
|---------|-----|-----|------|
| RDS instance | db.t4g.micro | db.t4g.small | db.t4g.medium |
| Fargate CPU/Mem | 256/512 | 512/1024 | 1024/2048 |
| Task count | 1 | 1 | 2 |
| Domain | strava-dev.codinario.com | strava-uat.codinario.com | strava.codinario.com |
| Log retention | 30 days | 30 days | 90 days |
