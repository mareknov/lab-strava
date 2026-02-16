export interface EnvironmentConfig {
  account: string;
  dbName: string;
  desiredTaskCount: number;
  domainName: string;
  fargateTaskCpu: number;
  fargateTaskMemoryMiB: number;
  rdsInstanceClass: string;
  region: string;
}

const account = process.env.CDK_DEFAULT_ACCOUNT;
const region = process.env.CDK_DEFAULT_REGION || "eu-west-1";

if (!account) {
  throw new Error(
    "CDK_DEFAULT_ACCOUNT is not set. Configure AWS credentials (aws configure) or set the environment variable.",
  );
}

export const environments: Record<string, EnvironmentConfig> = {
  dev: {
    account,
    dbName: "lab_strava_dev_db",
    desiredTaskCount: 1,
    domainName: "strava-dev.codinario.com",
    fargateTaskCpu: 256,
    fargateTaskMemoryMiB: 512,
    rdsInstanceClass: "db.t4g.micro",
    region,
  },
  uat: {
    account,
    dbName: "lab_strava_uat_db",
    desiredTaskCount: 1,
    domainName: "strava-uat.codinario.com",
    fargateTaskCpu: 512,
    fargateTaskMemoryMiB: 1024,
    rdsInstanceClass: "db.t4g.small",
    region,
  },
  prod: {
    account,
    dbName: "lab_strava_prod_db",
    desiredTaskCount: 2,
    domainName: "strava.codinario.com",
    fargateTaskCpu: 1024,
    fargateTaskMemoryMiB: 2048,
    rdsInstanceClass: "db.t4g.medium",
    region,
  },
};
