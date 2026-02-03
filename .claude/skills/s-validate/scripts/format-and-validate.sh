#!/bin/bash
set -euo pipefail

echo "=== Formatting ==="
./gradlew ktlintFormat

echo ""
echo "=== Compiling, Linting & Testing ==="
./gradlew clean check

echo ""
echo "=== Done ==="
