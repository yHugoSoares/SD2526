package org.example.client;

import java.util.InputMismatchException;
import java.util.Scanner;

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
                    case 1 -> handleRegister(scanner);
                    case 2 -> handleLogin(scanner);
                    case 3 -> handleAddEvent(scanner);
                    case 4 -> handleNextDay();
                    case 5 -> handleGetQuantitySales(scanner);
                    case 6 -> handleGetVolumeSales(scanner);
                    case 7 -> handleGetPriceStatistics(scanner);
                    case 8 -> {
                        System.out.println("Exiting...");
                        timeSeriesClient.disconnect();
                        return;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
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
        // Implementation for adding event
    }

    private void handleNextDay() {
        // Implementation for advancing to next day
    }

    private void handleGetQuantitySales(Scanner scanner) {
        // Implementation for getting quantity of sales
    }

    private void handleGetVolumeSales(Scanner scanner) {
        // Implementation for getting volume of sales
    }

    private void handleGetPriceStatistics(Scanner scanner) {
        // Implementation for getting price statistics
    }
}