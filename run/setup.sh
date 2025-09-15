#!/usr/bin/env bash
set -e
export JAVA_TOOL_OPTIONS="-Xmx2g"
if [ -f mvnw ]; then
  ./mvnw -q -DskipTests package
elif command -v mvn >/dev/null 2>&1; then
  mvn -q -DskipTests package
fi
