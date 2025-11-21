# SD2526

Small educational time-series server with a binary protocol, client library and simple persistence.

## Overview
- Server entrypoint: [`Main`](app/src/main/java/Main.java) — starts the time-series server implemented in [`server.TimeSeriesServer`](app/src/main/java/server/TimeSeriesServer.java).
- Per-connection handler: [`server.ClientHandler`](app/src/main/java/server/ClientHandler.java).
- In-memory per-day series: [`domain.TimeSeries`](app/src/main/java/domain/TimeSeries.java) storing [`domain.Event`](app/src/main/java/domain/Event.java).
- User model & auth helpers: [`domain.User`](app/src/main/java/domain/User.java).
- Aggregation cache structures: [`domain.AggregationResult`](app/src/main/java/domain/AggregationResult.java).
- Notification utilities (wait for simultaneous / consecutive sales): [`server.NotificationManager`](app/src/main/java/server/NotificationManager.java).
- Client API: [`client.TimeSeriesClient`](app/src/main/java/client/TimeSeriesClient.java) and sample registration tool [`client.RegisterUser`](app/src/main/java/client/RegisterUser.java).
- Transport / compact serialization: [`protocol.BinaryProtocol`](app/src/main/java/protocol/BinaryProtocol.java) and command ids in [`protocol.ProtocolCommands`](app/src/main/java/protocol/ProtocolCommands.java).

## Build
Requires Java toolchain (configured to Java 21 in the module). Build with Gradle wrapper:
```sh
./gradlew :app:build