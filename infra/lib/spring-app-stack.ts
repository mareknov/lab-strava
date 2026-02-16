import * as cdk from "aws-cdk-lib";
import * as ec2 from "aws-cdk-lib/aws-ec2";
import * as rds from "aws-cdk-lib/aws-rds";
import * as ecs from "aws-cdk-lib/aws-ecs";
import * as ecsPatterns from "aws-cdk-lib/aws-ecs-patterns";
import * as acm from "aws-cdk-lib/aws-certificatemanager";
import * as logs from "aws-cdk-lib/aws-logs";
import {Construct} from "constructs";
import {EnvironmentConfig} from "../config/env-config";
import {isProd} from "../utils/env-utils";

interface SpringAppStackProps extends cdk.StackProps {
  envConfig: EnvironmentConfig;
  envName: string;
  vpc: ec2.IVpc;
  dbInstance: rds.DatabaseInstance;
  dbSecret: rds.DatabaseSecret;
  rdsSg: ec2.SecurityGroup;
}

export class SpringAppStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props: SpringAppStackProps) {
    super(scope, id, props);

    const {envConfig, envName, vpc, dbInstance, dbSecret, rdsSg} = props;

    // 1. Security Groups
    const albSg = new ec2.SecurityGroup(this, "AlbSecurityGroup", {
      vpc,
      description: "ALB Security Group",
      allowAllOutbound: true,
    });
    albSg.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(80), "HTTP");
    albSg.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(443), "HTTPS");

    const ecsSg = new ec2.SecurityGroup(this, "EcsSecurityGroup", {
      vpc,
      description: "ECS Security Group",
      allowAllOutbound: true,
    });
    ecsSg.addIngressRule(albSg, ec2.Port.tcp(8080), "From ALB");

    // Allow ECS to reach RDS
    new ec2.CfnSecurityGroupIngress(this, "RdsIngressFromEcs", {
      groupId: rdsSg.securityGroupId,
      sourceSecurityGroupId: ecsSg.securityGroupId,
      ipProtocol: "tcp",
      fromPort: 5432,
      toPort: 5432,
      description: "From ECS",
    });

    // 2. ACM Certificate
    const certificate = new acm.Certificate(this, "Certificate", {
      domainName: envConfig.domainName,
      validation: acm.CertificateValidation.fromDns(),
    });

    // 2. CloudWatch Log Group
    const logGroup = new logs.LogGroup(this, "LogGroup", {
      logGroupName: `/ecs/lab-strava-${envName}`,
      retention: isProd(envName) ? logs.RetentionDays.THREE_MONTHS : logs.RetentionDays.ONE_MONTH,
      removalPolicy: isProd(envName) ? cdk.RemovalPolicy.RETAIN : cdk.RemovalPolicy.DESTROY,
    });

    // 3. ECS Cluster
    const cluster = new ecs.Cluster(this, "Cluster", {
      vpc,
      clusterName: `lab-strava-${envName}`,
    });

    // 4. ECS Fargate Service with ALB
    const fargateService = new ecsPatterns.ApplicationLoadBalancedFargateService(
      this,
      "FargateService",
      {
        cluster,
        cpu: envConfig.fargateTaskCpu,
        memoryLimitMiB: envConfig.fargateTaskMemoryMiB,
        desiredCount: envConfig.desiredTaskCount,
        certificate,
        redirectHTTP: true,
        taskImageOptions: {
          image: ecs.ContainerImage.fromAsset("../"),
          containerPort: 8080,
          environment: {
            SPRING_PROFILES_ACTIVE: envName,
            SPRING_DATASOURCE_URL: `jdbc:postgresql://${dbInstance.dbInstanceEndpointAddress}:5432/${envConfig.dbName}`,
            SERVER_PORT: "8080",
          },
          secrets: {
            SPRING_DATASOURCE_USERNAME: ecs.Secret.fromSecretsManager(dbSecret, "username"),
            SPRING_DATASOURCE_PASSWORD: ecs.Secret.fromSecretsManager(dbSecret, "password"),
          },
          logDriver: ecs.LogDrivers.awsLogs({
            logGroup,
            streamPrefix: "ecs",
          }),
        },
        securityGroups: [ecsSg],
        assignPublicIp: true,
      },
    );

    // Configure ALB security group
    fargateService.loadBalancer.addSecurityGroup(albSg);

    // Health check configuration
    fargateService.targetGroup.configureHealthCheck({
      path: "/api/v1/users",
      healthyHttpCodes: "200",
      interval: cdk.Duration.seconds(30),
      timeout: cdk.Duration.seconds(10),
      healthyThresholdCount: 2,
      unhealthyThresholdCount: 3,
    });

    // 5. Outputs
    new cdk.CfnOutput(this, "AlbDnsName", {
      value: fargateService.loadBalancer.loadBalancerDnsName,
      description: "ALB DNS name (use as CNAME target at Websupport)",
    });

    new cdk.CfnOutput(this, "EcsClusterName", {
      value: cluster.clusterName,
      description: "ECS cluster name",
    });

    new cdk.CfnOutput(this, "LogGroupName", {
      value: logGroup.logGroupName,
      description: "CloudWatch log group name",
    });

    new cdk.CfnOutput(this, "CertificateArn", {
      value: certificate.certificateArn,
      description: "ACM certificate ARN",
    });
  }
}
