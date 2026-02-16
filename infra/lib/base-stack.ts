import * as cdk from "aws-cdk-lib";
import * as ec2 from "aws-cdk-lib/aws-ec2";
import * as rds from "aws-cdk-lib/aws-rds";
import {Construct} from "constructs";
import {EnvironmentConfig} from "../config/env-config";
import {isProd} from "../utils/env-utils";

interface BaseStackProps extends cdk.StackProps {
  envConfig: EnvironmentConfig;
  envName: string;
}

export class BaseStack extends cdk.Stack {
  readonly vpc: ec2.IVpc;
  readonly dbInstance: rds.DatabaseInstance;
  readonly dbSecret: rds.DatabaseSecret;
  readonly rdsSg: ec2.SecurityGroup;

  constructor(scope: Construct, id: string, props: BaseStackProps) {
    super(scope, id, props);

    const {envConfig, envName} = props;

    // 1. Default VPC Lookup
    this.vpc = ec2.Vpc.fromLookup(this, "DefaultVpc", {isDefault: true});

    // 2. Security Groups
    this.rdsSg = new ec2.SecurityGroup(this, "RdsSecurityGroup", {
      vpc: this.vpc,
      description: "RDS Security Group",
      allowAllOutbound: false,
    });

    // 3. RDS PostgreSQL
    this.dbSecret = new rds.DatabaseSecret(this, "DbSecret", {
      username: "lab_strava_user",
      secretName: `lab-strava-${envName}-db-secret`,
    });

    this.dbInstance = new rds.DatabaseInstance(this, "Database", {
      engine: rds.DatabaseInstanceEngine.postgres({
        version: rds.PostgresEngineVersion.VER_15,
      }),
      instanceType: new ec2.InstanceType(envConfig.rdsInstanceClass.replace("db.", "")),
      vpc: this.vpc,
      vpcSubnets: {subnetType: ec2.SubnetType.PUBLIC},
      securityGroups: [this.rdsSg],
      credentials: rds.Credentials.fromSecret(this.dbSecret),
      databaseName: envConfig.dbName,
      multiAz: isProd(envName),
      publiclyAccessible: false,
      deletionProtection: isProd(envName),
      removalPolicy: isProd(envName) ? cdk.RemovalPolicy.RETAIN : cdk.RemovalPolicy.DESTROY,
      allocatedStorage: 20,
      maxAllocatedStorage: 100,
      backupRetention: cdk.Duration.days(isProd(envName) ? 7 : 1),
    });

    // Outputs
    new cdk.CfnOutput(this, "RdsEndpoint", {
      value: this.dbInstance.dbInstanceEndpointAddress,
      description: "RDS endpoint address",
    });
  }
}
