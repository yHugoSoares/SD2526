package org.example.Server;

import java.net.*;
import java.io.*;

/**
 * Programa servidor principal
 * Inicia servidor de séries temporais com parametrização
 */
public class Server {    
    public static void main(String[] args) {
        // Use try-with-resources so ServerSocket is always closed and handle IOException
        int port = 7575;
        int maxDays = 30;
        int maxSeriesInMemory = 5;

        if (args.length > 0) port = Integer.parseInt(args[0]);
        if (args.length > 1) maxDays = Integer.parseInt(args[1]);
        if (args.length > 2) maxSeriesInMemory = Integer.parseInt(args[2]);

        TimeSeriesServer server = new TimeSeriesServer(port, maxDays, maxSeriesInMemory);

        System.out.println("Iniciando servidor...");
        System.out.println("Porta: " + port);
        System.out.println("Dias máximos: " + maxDays);
        System.out.println("Séries em memória: " + maxSeriesInMemory);

        server.start();
    }
}
