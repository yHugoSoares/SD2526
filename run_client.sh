#!/bin/bash
# Simple Gradle-aware client launcher
# Usage: ./run_client.sh [HOST] [PORT] [USERNAME] [PASSWORD]

HOST=${1:-localhost}
PORT=${2:-8585}

set -euo pipefail

echo "[CLIENT] Building project..."
./gradlew -q build

MAIN_CP="app/build/classes/java/main:app/build/libs/*"

# Prefer interactive console if available
if [ -f "app/build/classes/java/main/client/ClientConsole.class" ] || [ -f "app/src/main/java/client/ClientConsole.java" ]; then
  echo "[CLIENT] Running interactive console (client.ClientConsole)..."
  exec java -cp "$MAIN_CP" client.ClientConsole "$HOST" "$PORT"
else
  echo "[CLIENT] Interactive console not found — running CLI client.RegisterUser"
  USERNAME=${3:-newuser}
  PASSWORD=${4:-pass}
  exec java -cp "$MAIN_CP" client.RegisterUser "$USERNAME" "$PASSWORD" "$PORT"
fi