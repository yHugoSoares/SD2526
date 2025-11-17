#!/bin/bash
# Script para executar servidor

PORT=${1:-5000}
D_PARAM=${2:-7}
S_PARAM=${3:-3}

echo "[SERVER] Compilando..."
make build

echo "[SERVER] Iniciando servidor na porta $PORT..."
# Check if the port is available before attempting to bind
if lsof -i :${PORT} -P -n >/dev/null 2>&1; then
	echo "[ERROR] Port $PORT is already in use by the following process(es):"
	lsof -i :${PORT} -P -n
	echo "If this is a system service (e.g. macOS Control Center) it may restart automatically."
	echo "Start the server on a different port: ./run_server.sh 5001"
	exit 1
fi

java -cp build/classes Main $PORT $D_PARAM $S_PARAM
