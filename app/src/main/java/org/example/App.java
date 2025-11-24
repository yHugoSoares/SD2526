package org.example;

import org.example.client.ClientUI;
import org.example.server.Server;
import org.example.client.TimeSeriesClient;

/**
 * Programa principal
 * Choose to run as server or client based on args
 */
public class App {

    private static final String DEFAULT_PORT = "7575";
    private static final String DEFAULT_MAX_DAYS = "30";
    private static final String DEFAULT_MAX_SERIES_IN_MEMORY = "5";
    private static final String CLIENT_MODE = "client";
    private static final String SERVER_MODE = "server";

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.err.println("Usage: java App <client|server> [options]");
                return;
            }

            String mode = args[0].toLowerCase();
            switch (mode) {
                case CLIENT_MODE: startClient(args); break;
                case SERVER_MODE: startServer(args); break;
                default: System.err.println("Invalid mode. Use 'client' or 'server'."); break;
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void startClient(String[] args) {
        try {
            String portStr = args.length > 1 ? args[1] : DEFAULT_PORT;
            int port = Integer.parseInt(portStr);

            System.out.println("Starting client on port " + port + "...");
            ClientUI clientUI = new ClientUI(new TimeSeriesClient("localhost", port));
            clientUI.chooseOption();
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number for client: " + args[1]);
        } catch (Exception e) {
            System.err.println("Failed to start client: " + e.getMessage());
        }
    }

    private static void startServer(String[] args) {
        try {
            String port = args.length > 1 ? args[1] : DEFAULT_PORT;
            String maxDays = args.length > 2 ? args[2] : DEFAULT_MAX_DAYS;
            String maxSeriesInMemory = args.length > 3 ? args[3] : DEFAULT_MAX_SERIES_IN_MEMORY;

            Server serverApp = new Server();
            serverApp.startServer(port, maxDays, maxSeriesInMemory);
        } catch (NumberFormatException e) {
            System.err.println("Invalid numeric argument for server: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}