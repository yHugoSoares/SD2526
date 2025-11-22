/*
 * This file is the main of the application. It decides whether to run in client
 * mode or server mode based on command-line arguments.
 */
package org.example;

public class App {
    public static void main(String[] args) {
        if (args.length > 0) {
            String mode = args[0].toLowerCase();
            int port = 8080; // Default port
            int maxDays = 30; // Default maxDays
            int maxSeriesInMemory = 100; // Default maxSeriesInMemory

            // Parse additional arguments if provided
            try {
                if (args.length > 1) port = Integer.parseInt(args[1]);
                if (args.length > 2) maxDays = Integer.parseInt(args[2]);
                if (args.length > 3) maxSeriesInMemory = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format for port, maxDays, or maxSeriesInMemory.");
                return;
            }

            // Decide mode
            if ("client".equals(mode)) {
                runClient(port, maxDays, maxSeriesInMemory);
            } else if ("server".equals(mode)) {
                runServer(port, maxDays, maxSeriesInMemory);
            } else {
                System.out.println("Invalid argument. Use 'client' or 'server'.");
            }
        } else {
            System.out.println("No arguments provided. Use 'client' or 'server' followed by optional port, maxDays, and maxSeriesInMemory.");
        }
    }

    private static void runClient(int port, int maxDays, int maxSeriesInMemory) {
        System.out.println("Running client...");
        System.out.println("Port: " + port);
        System.out.println("Max Days: " + maxDays);
        System.out.println("Max Series In Memory: " + maxSeriesInMemory);
        // Add client-specific logic here
    }

    private static void runServer(int port, int maxDays, int maxSeriesInMemory) {
        System.out.println("Running server...");
        System.out.println("Port: " + port);
        System.out.println("Max Days: " + maxDays);
        System.out.println("Max Series In Memory: " + maxSeriesInMemory);
        // Add server-specific logic here
    }
}