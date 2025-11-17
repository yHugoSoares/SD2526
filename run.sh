#!/bin/bash
# Script para executar o sistema facilmente

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

PORT=${PORT:-12345}
D_PARAM=${D_PARAM:-7}
S_PARAM=${S_PARAM:-3}

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

show_menu() {
    clear
    echo -e "${BLUE}=== Sistema de Séries Temporais em Java ===${NC}"
    echo ""
    echo "Escolha uma opção:"
    echo "1. Compilar projeto"
    echo "2. Executar servidor"
    echo "3. Executar cliente interativo"
    echo "4. Executar testes de desempenho"
    echo "5. Compilar e executar servidor"
    echo "6. Limpar compilação"
    echo "0. Sair"
    echo ""
    read -p "Opção: " choice
}

build_project() {
    echo -e "${GREEN}[BUILD] Compilando projeto...${NC}"
    make build
    echo -e "${GREEN}[BUILD] Sucesso!${NC}"
}

run_server() {
    echo -e "${GREEN}[SERVER] Iniciando servidor...${NC}"
    echo "Porta: $PORT"
    echo "Dias anteriores (D): $D_PARAM"
    echo "Séries em memória (S): $S_PARAM"
    make run-server PORT=$PORT D_PARAM=$D_PARAM S_PARAM=$S_PARAM
}

run_client() {
    echo -e "${GREEN}[CLIENT] Iniciando cliente...${NC}"
    make run-client
}

run_tests() {
    echo -e "${GREEN}[TEST] Executando testes...${NC}"
    make test
}

clean_build() {
    echo -e "${GREEN}[CLEAN] Limpando build...${NC}"
    make clean
    echo -e "${GREEN}[CLEAN] Concluído!${NC}"
}

# Menu principal
while true; do
    show_menu
    case $choice in
        1) build_project ;;
        2) run_server ;;
        3) run_client ;;
        4) run_tests ;;
        5) build_project && run_server ;;
        6) clean_build ;;
        0) echo "Saindo..."; exit 0 ;;
        *) echo "Opção inválida"; sleep 1 ;;
    esac
done
