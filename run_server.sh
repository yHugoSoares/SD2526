#!/bin/bash
# Build and run the server using Gradle-built classes
# Usage: ./run_server.sh [PORT] [D] [S]

PORT=${1:-8585}
D_PARAM=${2:-7}
S_PARAM=${3:-3}

set -euo pipefail

echo "[SERVER] Building project with Gradle..."
./gradlew -q build

echo "[SERVER] Starting server on port $PORT (D=$D_PARAM, S=$S_PARAM)..."

# Check if port is in use
if command -v lsof >/dev/null 2>&1; then
  if lsof -i :${PORT} -P -n >/dev/null 2>&1; then
    echo "[ERROR] Port $PORT is already in use:" >&2
    lsof -i :${PORT} -P -n
    exit 1
  fi
fi

# Build classpath: compiled main classes and any built libs/jars
CLASSPATH="app/build/classes/java/main:app/build/libs/*"

exec java -cp "$CLASSPATH" Main "$PORT" "$D_PARAM" "$S_PARAM"
