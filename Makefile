# Makefile para Sistema de Séries Temporais em Java
# Compilação e execução do servidor e cliente

JAVA_HOME ?= /usr/lib/jvm/java-11-openjdk-amd64
JAVAC = javac
JAVA = java
JAR = jar

# Diretórios
SRC_DIR = src/main/java
TEST_DIR = src/test/java
BUILD_DIR = build
CLASSES_DIR = $(BUILD_DIR)/classes
TEST_CLASSES_DIR = $(BUILD_DIR)/test-classes
LIB_DIR = lib
DATA_DIR = data
PORT = 12345
D_PARAM = 7
S_PARAM = 3

# Fonte Java - Core
SOURCES = \
	$(SRC_DIR)/domain/Event.java \
	$(SRC_DIR)/domain/User.java \
	$(SRC_DIR)/domain/AggregationResult.java \
	$(SRC_DIR)/domain/TimeSeries.java \
	$(SRC_DIR)/protocol/ProtocolCommands.java \
	$(SRC_DIR)/protocol/BinaryProtocol.java \
	$(SRC_DIR)/server/TimeSeriesServer.java \
	$(SRC_DIR)/server/ClientHandler.java \
	$(SRC_DIR)/server/NotificationManager.java \
	$(SRC_DIR)/client/TimeSeriesClient.java \
	$(SRC_DIR)/Main.java

# Testes
TEST_SOURCES = \
	$(TEST_DIR)/TestClientUI.java \
	$(TEST_DIR)/PerformanceTest.java

# Alvos principais
.PHONY: all build clean run-server run-client test help rebuild

all: build

help:
	@echo "=== Sistema de Séries Temporais em Java ==="
	@echo ""
	@echo "Comandos disponíveis:"
	@echo "  make build          - Compila o projeto"
	@echo "  make run-server     - Executa o servidor (porta $(PORT))"
	@echo "  make run-client     - Executa cliente interativo"
	@echo "  make test           - Executa testes de desempenho"
	@echo "  make clean          - Remove arquivos compilados"
	@echo "  make rebuild        - Limpa e recompila"
	@echo ""
	@echo "Variáveis de configuração:"
	@echo "  PORT=$(PORT)        - Porta do servidor"
	@echo "  D_PARAM=$(D_PARAM)  - Número de dias anteriores"
	@echo "  S_PARAM=$(S_PARAM)  - Limite de séries em memória"
	@echo ""

build: $(CLASSES_DIR) $(SOURCES)
	@echo "[BUILD] Compilando código fonte..."
	$(JAVAC) -d $(CLASSES_DIR) -cp $(CLASSES_DIR) $(SOURCES)
	@echo "[BUILD] Compilação concluída com sucesso"

$(CLASSES_DIR):
	@mkdir -p $(CLASSES_DIR)
	@mkdir -p $(DATA_DIR)

run-server: build
	@echo "[SERVER] Iniciando servidor na porta $(PORT)..."
	@echo "[SERVER] Dias anteriores (D): $(D_PARAM)"
	@echo "[SERVER] Séries em memória (S): $(S_PARAM)"
	$(JAVA) -cp $(CLASSES_DIR) Main $(PORT) $(D_PARAM) $(S_PARAM)

run-client: build
	@echo "[CLIENT] Compilando cliente..."
	$(JAVAC) -d $(CLASSES_DIR) -cp $(CLASSES_DIR) $(TEST_DIR)/TestClientUI.java
	@echo "[CLIENT] Iniciando cliente..."
	$(JAVA) -cp $(CLASSES_DIR) TestClientUI localhost $(PORT)

test: build
	@echo "[TEST] Compilando testes..."
	$(JAVAC) -d $(CLASSES_DIR) -cp $(CLASSES_DIR) $(TEST_DIR)/PerformanceTest.java
	@echo "[TEST] Executando testes..."
	$(JAVA) -cp $(CLASSES_DIR) PerformanceTest

clean:
	@echo "[CLEAN] Removendo diretório de compilação..."
	@rm -rf $(BUILD_DIR)
	@echo "[CLEAN] Limpeza concluída"

rebuild: clean build
	@echo "[REBUILD] Reconstrução completa concluída"

.SILENT: help
