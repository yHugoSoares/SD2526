#!/bin/bash
# Script interativo para compilar e executar

echo "=== Sistema de Séries Temporais em Java ==="
echo ""
echo "1. Compilar"
echo "2. Executar servidor"
echo "3. Executar cliente"
echo "4. Executar testes"
echo "5. Limpar"
echo "6. Sair"
echo ""
read -p "Escolha: " choice

case $choice in
    1)
        make clean
        make build
        ;;
    2)
        read -p "Porta [5000]: " port
        port=${port:-5000}
        read -p "Dias [7]: " days
        days=${days:-7}
        read -p "Séries em memória [3]: " series
        series=${series:-3}
        make PORT=$port D_PARAM=$days S_PARAM=$series run-server
        ;;
    3)
        read -p "Host [localhost]: " host
        host=${host:-localhost}
        read -p "Porta [5000]: " port
        port=${port:-5000}
        bash run_client.sh $host $port
        ;;
    4)
        bash run_tests.sh
        ;;
    5)
        make clean
        echo "Limpeza concluída!"
        ;;
    6)
        echo "Adeus!"
        exit 0
        ;;
    *)
        echo "Opção inválida"
        ;;
esac
