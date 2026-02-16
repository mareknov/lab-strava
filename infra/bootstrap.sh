#!/usr/bin/env bash
set -euo pipefail

AWS_PROFILE="${AWS_PROFILE:-codinario-dev}"
export AWS_PROFILE

echo "Bootstrapping CDK with profile: $AWS_PROFILE"

cd "$(dirname "$0")"
npm install
npx cdk bootstrap
