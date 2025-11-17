#!/bin/bash
# Script para desenvolvimento - servidor + cliente em terminal separado

make build

# Terminal 1: Servidor
echo "Iniciando servidor..."
gnome-terminal -- bash -c "cd '$(pwd)' && make run-server; exec bash" &
TERM_PID=$!

# Aguarda servidor iniciar
sleep 2

# Terminal 2: Cliente
echo "Iniciando cliente..."
gnome-terminal -- bash -c "cd '$(pwd)' && make run-client; exec bash" &

# Aguarda sair
wait $TERM_PID
