package org.example.server;

import java.io.IOException;

public class Server {

    public void startServer(String portS, String maxDaysS, String maxSeriesInMemoryS) throws IOException {
        // Start server
        int port = Integer.parseInt(portS);
        int maxDays = Integer.parseInt(maxDaysS);
        int maxSeriesInMemory = Integer.parseInt(maxSeriesInMemoryS);

        TimeSeriesServer server = new TimeSeriesServer(port, maxDays, maxSeriesInMemory);

        System.out.println("Iniciando servidor...");
        System.out.println("Porta: " + port);
        System.out.println("Dias máximos: " + maxDays);
        System.out.println("Séries em memória: " + maxSeriesInMemory);

        server.start();
    }
}
