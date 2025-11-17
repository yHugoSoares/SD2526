# Time Series Server (Java)

Servidor/cliente em Java para registo e agregação de eventos de venda por dia. Inclui cliente interativo, protocolo binário simples e testes de desempenho.

## Funcionalidade principal
- Servidor TCP multi-threaded que gere séries temporais por dia, persistência em disco e autenticação de utilizadores.
  - Implementação principal: [`server.TimeSeriesServer`](src/main/java/server/TimeSeriesServer.java)
- Handler por cliente com comandos binários (registo, login, adicionar evento, agregações, esperas/notifications).
  - Implementação: [`server.ClientHandler`](src/main/java/server/ClientHandler.java)
- Gestão de notificações (vendas simultâneas / consecutivas) com Conditions.
  - Implementação: [`server.NotificationManager`](src/main/java/server/NotificationManager.java)
- Representações de domínio: evento, utilizador, resultados de agregação, série temporal.
  - Eventos: [`domain.Event`](src/main/java/domain/Event.java)
  - Utilizador: [`domain.User`](src/main/java/domain/User.java)
  - Agregação: [`domain.AggregationResult`](src/main/java/domain/AggregationResult.java)
  - Série temporal por dia: [`domain.TimeSeries`](src/main/java/domain/TimeSeries.java)
- Biblioteca cliente com API simples e bloqueante para operações remotas.
  - Implementação: [`client.TimeSeriesClient`](src/main/java/client/TimeSeriesClient.java)
- Protocolo binário e utilitários de serialização de eventos.
  - Comandos: [`protocol.ProtocolCommands`](src/main/java/protocol/ProtocolCommands.java)
  - Serialização compacta: [`protocol.BinaryProtocol`](src/main/java/protocol/BinaryProtocol.java)

## Scripts / Make targets
- Compilar: `make build` (ver [Makefile](Makefile))
- Executar servidor: `make run-server PORT=<port> D_PARAM=<days> S_PARAM=<series>` or use [run_server.sh](run_server.sh)
- Executar cliente interativo: `make run-client` or use [run_client.sh](run_client.sh)
- Executar testes de performance: `make test` or use [run_tests.sh](run_tests.sh)
- UI / testes locais:
  - Cliente UI: [`test.TestClientUI`](src/test/java/TestClientUI.java)
  - Testes de performance: [`test.PerformanceTest`](src/test/java/PerformanceTest.java)

## Quickstart (local)
1. Compilar:
   ```sh
   make build