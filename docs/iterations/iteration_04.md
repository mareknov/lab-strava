# Iteration 04

The intention of this iteration is to prepare infrastructures for the SpringBoot app to run in AWS.

This should include

- ECS cluster with fargate nodes
- ALB to expose the SpringBoot app
- CloudWatch group
- RDS with Postgres DB

## Requirements

- all infra code should go to `infra` directory
- make the deployment env specific (`dev`, `uat`, `prod`)
- use Typescript CDK
- use default VPC
- set up ALB so it can be accessible publicly
- I want to map ALB to domain `strava.codinario.com` that I have registered with Websupport (find this on web)

## Open questions

- Is there a framework that can help me with CDK development and deployment?
- Think first, and if there is a better approach to what I described above, ask me first