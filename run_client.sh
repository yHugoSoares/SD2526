#!/bin/bash
# Script para executar cliente interativo

# Se for chamado com um único argumento numérico, interpretá-lo como PORT
if [[ $# -eq 1 && "$1" =~ ^[0-9]+$ ]]; then
    HOST=localhost
    PORT=$1
else
    HOST=${1:-localhost}
    PORT=${2:-5000}
fi

echo "[CLIENT] Compilando..."
make build

echo "[CLIENT] Iniciando cliente em $HOST:$PORT..."

# Quick reachability check: try to open TCP connection (timeout 2s)
if ! timeout 2 bash -c "</dev/tcp/$HOST/$PORT" >/dev/null 2>&1; then
    echo "[WARN] Cannot reach $HOST:$PORT (connection failed)."
    echo "Ensure the server is running on that host/port or start client with: ./run_client.sh <host> <port>"
    echo "Attempting to continue anyway..."
fi

# Compilar e executar UI de teste
javac -d build/classes -cp build/classes src/test/java/TestClientUI.java

java -cp build/classes TestClientUI $HOST $PORT