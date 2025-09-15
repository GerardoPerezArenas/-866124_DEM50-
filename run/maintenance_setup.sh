#!/usr/bin/env bash
set -e
if [ -f mvnw ]; then
  ./mvnw -q -DskipTests verify
elif command -v mvn >/dev/null 2>&1; then
  mvn -q -DskipTests verify
fi
