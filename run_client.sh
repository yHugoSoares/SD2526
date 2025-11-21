#!/bin/bash
# Script para executar cliente interativo

# Se for chamado com um único argumento numérico, interpretá-lo como PORT
if [[ $# -eq 1 && "$1" =~ ^[0-9]+$ ]]; then
    HOST=localhost
    PORT=$2345
else
    HOST=${1:-localhost}
    PORT=${2:-4999}
fi

echo "[CLIENT] Compilando..."
make build

echo "[CLIENT] Iniciando cliente em $HOST:$PORT..."

# Quick reachability check: prefer `nc` (netcat), otherwise fallback to
# a small retry loop using /dev/tcp. This reduces false negatives when
# the server is slow to accept connections or /dev/tcp behaves oddly.
reachable=false
if command -v nc >/dev/null 2>&1; then
    if nc -z -w2 "$HOST" "$PORT" >/dev/null 2>&1; then
        reachable=true
    fi
else
    # fallback: retry a few times with a short timeout
    for i in 1 2 3; do
        if timeout 2 bash -c ">/dev/tcp/$HOST/$PORT" >/dev/null 2>&1; then
            reachable=true
            break
        fi
        sleep 1
    done
fi

if ! $reachable; then
    echo "[WARN] Cannot reach $HOST:$PORT (connection failed after checks)."
    echo "Ensure the server is running on that host/port or start client with: ./run_client.sh <host> <port>"
    echo "Attempting to continue anyway..."
fi

# Compilar e executar UI de teste
javac -d build/classes -cp build/classes src/test/java/TestClientUI.java

java -cp build/classes TestClientUI $HOST $PORT