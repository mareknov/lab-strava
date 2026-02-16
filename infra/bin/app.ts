#!/usr/bin/env node
import * as cdk from "aws-cdk-lib";
import {BaseStack} from "../lib/base-stack";
import {SpringAppStack} from "../lib/spring-app-stack";
import {environments} from "../config/env-config";

const app = new cdk.App();

for (const [envName, envConfig] of Object.entries(environments)) {
  const env = {account: envConfig.account, region: envConfig.region};

  const baseStack = new BaseStack(app, `LabStrava-${envName}-base`, {
    env,
    envConfig,
    envName,
  });

  new SpringAppStack(app, `LabStrava-${envName}-app`, {
    env,
    envConfig,
    envName,
    vpc: baseStack.vpc,
    dbInstance: baseStack.dbInstance,
    dbSecret: baseStack.dbSecret,
    rdsSg: baseStack.rdsSg,
  });
}
