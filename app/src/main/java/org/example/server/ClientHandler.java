package org.example.server;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import org.example.domain.*;
import org.example.protocol.*;

/**
 * Handler para cada cliente conectado
 * Gerencia autenticação e processamento de comandos
 */
public class ClientHandler implements Runnable {

    private final TimeSeriesServer server;
    private final Socket socket;
    private boolean authenticated = false;
    private String currentUsername = null;
    private final ReentrantLock connectionLock = new ReentrantLock();

    public ClientHandler(TimeSeriesServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(
                socket.getOutputStream()
            );

            while (true) {
                connectionLock.lock();
                try {
                    int commandId = dis.readInt();
                    System.out.println(
                        "[SERVER] Received command ID: " + commandId
                    );

                    if (
                        !authenticated &&
                        commandId != ProtocolCommands.REGISTER &&
                        commandId != ProtocolCommands.LOGIN
                    ) {
                        System.out.println(
                            "[SERVER] Unauthenticated attempt to execute command: " +
                                commandId
                        );
                        dos.writeInt(ProtocolCommands.RESPONSE_ERROR);
                        dos.writeUTF("Não autenticado");
                        dos.flush();
                        continue;
                    }

                    switch (commandId) {
                        case ProtocolCommands.REGISTER:
                            handleRegister(dis, dos);
                            break;
                        case ProtocolCommands.LOGIN:
                            handleLogin(dis, dos);
                            break;
                        case ProtocolCommands.ADD_EVENT:
                            handleAddEvent(dis, dos);
                            break;
                        case ProtocolCommands.NEXT_DAY:
                            handleNextDay(dis, dos);
                            break;
                        case ProtocolCommands.GET_QUANTITY:
                            handleGetQuantity(dis, dos);
                            break;
                        case ProtocolCommands.GET_VOLUME:
                            handleGetVolume(dis, dos);
                            break;
                        case ProtocolCommands.GET_PRICE_STATS:
                            handleGetPriceStats(dis, dos);
                            break;
                        case ProtocolCommands.GET_EVENTS:
                            handleGetEvents(dis, dos);
                            break;
                        case ProtocolCommands.WAIT_SIMULTANEOUS:
                            handleWaitSimultaneous(dis, dos);
                            break;
                        case ProtocolCommands.WAIT_CONSECUTIVE:
                            handleWaitConsecutive(dis, dos);
                            break;
                        default:
                            dos.writeInt(ProtocolCommands.RESPONSE_ERROR);
                            dos.writeUTF("Comando desconhecido");
                            dos.flush();
                    }
                } finally {
                    connectionLock.unlock();
                }
            }
        } catch (EOFException e) {
            // Cliente desconectado normalmente
        } catch (IOException e) {
            System.err.println("Erro ao processar cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
            }
        }
    }

    private void handleRegister(DataInputStream dis, DataOutputStream dos)
        throws IOException {
        String username = dis.readUTF();
        String password = dis.readUTF();
        System.out.println("[SERVER] REGISTER - Username: " + username);

        if (server.registerUser(username, password)) {
            dos.writeInt(ProtocolCommands.RESPONSE_SUCCESS);
            dos.writeUTF("Registo bem-sucedido");
        } else {
            dos.writeInt(ProtocolCommands.RESPONSE_ERROR);
            dos.writeUTF("Utilizador já existe");
        }
        dos.flush();
    }

    private void handleLogin(DataInputStream dis, DataOutputStream dos)
        throws IOException {
        String username = dis.readUTF();
        String password = dis.readUTF();
        System.out.println("[SERVER] LOGIN - Username: " + username);

        if (server.authenticateUser(username, password)) {
            authenticated = true;
            currentUsername = username;
            dos.writeInt(ProtocolCommands.RESPONSE_SUCCESS);
            dos.writeUTF("Login bem-sucedido");
        } else {
            dos.writeInt(ProtocolCommands.RESPONSE_ERROR);
            dos.writeUTF("Utilizador ou password inválidos");
        }
        dos.flush();
    }

    private void handleAddEvent(DataInputStream dis, DataOutputStream dos)
        throws IOException {
        String productName = dis.readUTF();
        long quantity = dis.readLong();
        double price = dis.readDouble();
        System.out.println(
            "[SERVER] ADD_EVENT - Product: " +
                productName +
                ", Quantity: " +
                quantity +
                ", Price: " +
                price
        );

        server.addEvent(productName, quantity, price);

        dos.writeInt(ProtocolCommands.RESPONSE_SUCCESS);
        dos.writeUTF("Evento adicionado");
        dos.flush();
    }

    private void handleNextDay(DataInputStream dis, DataOutputStream dos)
        throws IOException {
        System.out.println("[SERVER] NEXT_DAY");
        server.nextDay();

        dos.writeInt(ProtocolCommands.RESPONSE_SUCCESS);
        dos.writeInt(server.getCurrentDay());
        dos.flush();
    }

    private void handleGetQuantity(DataInputStream dis, DataOutputStream dos)
        throws IOException {
        String product = dis.readUTF();
        int daysLookback = dis.readInt();
        System.out.println(
            "[SERVER] GET_QUANTITY - Product: " +
                product +
                ", Days Lookback: " +
                daysLookback
        );

        long quantity = 0;
        for (int i = 0; i <= daysLookback; i++) {
            int day = server.getCurrentDay() - i;
            if (day < 0) break;

            TimeSeries series = server.getTimeSeries(day);
            if (series != null) {
                quantity += series.calculateQuantity(
                    product,
                    daysLookback,
                    server.getCurrentDay()
                );
            }
        }

        dos.writeInt(ProtocolCommands.RESPONSE_SUCCESS);
        dos.writeLong(quantity);
        dos.flush();
    }

    private void handleGetVolume(DataInputStream dis, DataOutputStream dos)
        throws IOException {
        String product = dis.readUTF();
        int daysLookback = dis.readInt();
        System.out.println(
            "[SERVER] GET_VOLUME - Product: " +
                product +
                ", Days Lookback: " +
                daysLookback
        );

        double volume = 0;
        for (int i = 0; i <= daysLookback; i++) {
            int day = server.getCurrentDay() - i;
            if (day < 0) break;

            TimeSeries series = server.getTimeSeries(day);
            if (series != null) {
                double dayVolume = series.calculateVolume(
                    product,
                    daysLookback,
                    server.getCurrentDay()
                );
                System.out.println(
                    "[SERVER] Day " + day + " volume: " + dayVolume
                );
                volume += dayVolume;
            } else {
                System.out.println("[SERVER] Day " + day + " series is NULL");
            }
        }

        System.out.println("[SERVER] Total volume calculated: " + volume);
        dos.writeInt(ProtocolCommands.RESPONSE_SUCCESS);
        dos.writeDouble(volume);
        dos.flush();
    }

    private void handleGetPriceStats(DataInputStream dis, DataOutputStream dos)
        throws IOException {
        String product = dis.readUTF();
        int daysLookback = dis.readInt();
        System.out.println(
            "[SERVER] GET_PRICE_STATS - Product: " +
                product +
                ", Days Lookback: " +
                daysLookback
        );

        double sumAvg = 0;
        double maxPrice = 0;
        int count = 0;

        for (int i = 0; i <= daysLookback; i++) {
            int day = server.getCurrentDay() - i;
            if (day < 0) break;

            TimeSeries series = server.getTimeSeries(day);
            if (series != null) {
                AggregationResult.PriceStats stats = series.calculatePriceStats(
                    product,
                    daysLookback,
                    server.getCurrentDay()
                );
                
                // Only include in statistics if there were sales (max > 0)
                if (stats.maximum > 0) {
                    sumAvg += stats.average;
                    maxPrice = Math.max(maxPrice, stats.maximum);
                    count++;
                }
            }
        }

        dos.writeInt(ProtocolCommands.RESPONSE_SUCCESS);
        dos.writeDouble(sumAvg / Math.max(count, 1));
        dos.writeDouble(maxPrice);
        dos.flush();
    }

    private void handleGetEvents(DataInputStream dis, DataOutputStream dos)
        throws IOException {
        int dayOffset = dis.readInt();
        Set<String> products = new HashSet<>();
        int productCount = dis.readInt();
        for (int i = 0; i < productCount; i++) {
            products.add(dis.readUTF());
        }
        System.out.println(
            "[SERVER] GET_EVENTS - Day Offset: " +
                dayOffset +
                ", Products: " +
                products
        );

        int day = server.getCurrentDay() - dayOffset;
        TimeSeries series = server.getTimeSeries(day);

        List<Event> events = series != null
            ? series.getEventsForProducts(products)
            : new ArrayList<>();

        dos.writeInt(ProtocolCommands.RESPONSE_SUCCESS);
        dos.writeInt(events.size());
        for (Event event : events) {
            dos.writeUTF(event.getProductName());
            dos.writeLong(event.getQuantity());
            dos.writeDouble(event.getPrice());
        }
        dos.flush();
    }

    private void handleWaitSimultaneous(
        DataInputStream dis,
        DataOutputStream dos
    ) throws IOException {
        String product1 = dis.readUTF();
        String product2 = dis.readUTF();
        System.out.println(
            "[SERVER] WAIT_SIMULTANEOUS - Product1: " +
                product1 +
                ", Product2: " +
                product2
        );

        boolean result = server
            .getNotificationManager()
            .waitForSimultaneousSales(
                product1,
                product2,
                60000,
                server.getCurrentDay()
            );

        dos.writeInt(ProtocolCommands.RESPONSE_SUCCESS);
        dos.writeBoolean(result);
        dos.flush();
    }

    private void handleWaitConsecutive(
        DataInputStream dis,
        DataOutputStream dos
    ) throws IOException {
        String product = dis.readUTF();
        int count = dis.readInt();
        System.out.println(
            "[SERVER] WAIT_CONSECUTIVE - Product: " +
                product +
                ", Count: " +
                count
        );

        String result = server
            .getNotificationManager()
            .waitForConsecutiveSales(count, 60000);

        dos.writeInt(ProtocolCommands.RESPONSE_SUCCESS);
        dos.writeUTF(result != null ? result : "");
        dos.flush();
    }
}
