#!/usr/bin/env bash
set -euo pipefail

ENV="${1:-dev}"
AWS_PROFILE="${AWS_PROFILE:-codinario-dev}"
export AWS_PROFILE

echo "Deploying LabStrava-${ENV}-base and LabStrava-${ENV}-app with profile: $AWS_PROFILE"

cd "$(dirname "$0")"
npm install
npx cdk deploy "LabStrava-${ENV}-base" "LabStrava-${ENV}-app" --require-approval broadening
