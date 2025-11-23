package org.example.client;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Map;

public class ClientUI {

    private final TimeSeriesClient timeSeriesClient;

    public ClientUI(TimeSeriesClient timeSeriesClient) {
        this.timeSeriesClient = timeSeriesClient;
    }

    public void displayMenu() {
        System.out.println("=== Time Series Client Menu ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Add Event");
        System.out.println("4. Next Day");
        System.out.println("5. Get quantity of sales last N days");
        System.out.println("6. Get volume of sales last N days");
        System.out.println("7. Get price statistics");
        System.out.println("8. Exit");
    }

    public void chooseOption() {
        try (Scanner scanner = new Scanner(System.in)) {
            try {
                timeSeriesClient.connect();
            } catch (Exception e) {
                System.err.println("Failed to connect to the server: " + e.getMessage());
                return;
            }
            while (true) {
                displayMenu();
                int option = getUserInput(scanner);

                switch (option) {
                    case 1: handleRegister(scanner); break;
                    case 2: handleLogin(scanner); break;
                    case 3: handleAddEvent(scanner); break;
                    case 4: handleNextDay(); break;
                    case 5: handleGetQuantitySales(scanner); break;
                    case 6: handleGetVolumeSales(scanner); break;
                    case 7: handleGetPriceStatistics(scanner); break;
                    case 8: {
                        System.out.println("Exiting...");
                        timeSeriesClient.disconnect();
                        return;
                    }
                    default: System.out.println("Invalid option. Please try again."); break;
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private int getUserInput(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    private void handleRegister(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.next();
        System.out.print("Enter password: ");
        String password = scanner.next();

        try {
            if (timeSeriesClient.register(username, password)) {
                System.out.println("Registration successful!");
            } else {
                System.out.println("Registration failed. User may already exist.");
            }
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
        }
    }

    private void handleLogin(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.next();
        System.out.print("Enter password: ");
        String password = scanner.next();

        try {
            if (timeSeriesClient.login(username, password)) {
                System.out.println("Login successful!");
            } else {
                System.out.println("Login failed. Invalid credentials.");
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
        }
    }

    private void handleAddEvent(Scanner scanner) {
        // addEvent(String productName, long quantity, double price)
        System.out.print("Enter product name: ");
        String productName = scanner.next();
        System.out.print("Enter quantity: ");
        long quantity = scanner.nextLong();
        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        try {
            timeSeriesClient.addEvent(productName, quantity, price);
            System.out.println("Event added successfully!");
        } catch (Exception e) {
            System.err.println("Error adding event: " + e.getMessage());
        }
    }

    private void handleNextDay() {
        // Implementation for advancing to next day
        try {
            timeSeriesClient.nextDay();
            System.out.println("Advanced to next day successfully!");
        } catch (Exception e) {
            System.err.println("Error advancing to next day: " + e.getMessage());
        }
    }

    private void handleGetQuantitySales(Scanner scanner) {
        // Implementation for getting quantity of sales
        // getQuantity(String product, int daysLookback)
        System.out.print("Enter product name: ");
        String productName = scanner.next();
        System.out.print("Enter number of days to look back: ");
        int daysLookback = scanner.nextInt();
        try {
            long quantity = timeSeriesClient.getQuantity(productName, daysLookback);
            System.out.println("Total quantity sold in last " + daysLookback + "days:" + quantity);
        } catch (Exception e) {
            System.err.println("Error getting quantity of sales: " + e.getMessage());
        }
    }

    private void handleGetVolumeSales(Scanner scanner) {
        // Implementation for getting volume of sales
        // getVolume(String product, int daysLookback)
        System.out.print("Enter product name: ");
        String productName = scanner.next();
        System.out.print("Enter number of days to look back: ");
        int daysLookback = scanner.nextInt();
        try {
            double volume = timeSeriesClient.getVolume(productName, daysLookback);
            System.out.println("Total volume sold in last " + daysLookback + " days: " + volume);
        } catch (Exception e) {
            System.err.println("Error getting volume of sales: " + e.getMessage());
        }
    }
    private void handleGetPriceStatistics(Scanner scanner) {
        System.out.print("Enter product name: ");
        String productName = scanner.next();
        System.out.print("Enter number of days to look back: ");
        int daysLookback = scanner.nextInt();
        try {
            TimeSeriesClient.PriceStats priceStats = timeSeriesClient.getPriceStats(productName, daysLookback);
            System.out.println("Price statistics for " + productName + " in last " + daysLookback + " days:");
            System.out.println("Average price: " + priceStats.average);
            System.out.println("Maximum price: " + priceStats.maximum);
        } catch (InputMismatchException e) {
            System.err.println("Invalid input: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error getting price statistics: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error getting price statistics: " + e.getMessage());
        }
    }
}
