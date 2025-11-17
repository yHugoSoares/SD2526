#!/bin/bash
# Script para executar testes de desempenho

echo "[TEST] Compilando..."
make build

echo "[TEST] Compilando testes..."
javac -d build/classes -cp build/classes src/test/java/PerformanceTest.java

echo "[TEST] Executando testes de desempenho..."
java -cp build/classes PerformanceTest
