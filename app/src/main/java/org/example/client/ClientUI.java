package org.example.client;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class ClientUI {

    private final TimeSeriesClient timeSeriesClient;
    private boolean authenticated = false;

    public ClientUI(TimeSeriesClient timeSeriesClient) {
        this.timeSeriesClient = timeSeriesClient;
    }

    public void displayAuthMenu() {
        System.out.println("\n=== Time Series Client - Authentication ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }

    public void displayMainMenu() {
        System.out.println("\n=== Time Series Client - Main Menu ===");
        System.out.println("1. Add Event");
        System.out.println("2. Next Day");
        System.out.println("3. Get quantity of sales last N days");
        System.out.println("4. Get volume of sales last N days");
        System.out.println("5. Get price statistics");
        System.out.println("6. Logout");
        System.out.print("Choose an option: ");
    }

    public void chooseOption() {
        try (Scanner scanner = new Scanner(System.in)) {
            try {
                timeSeriesClient.connect();
                System.out.println("Connected to server successfully!");
            } catch (Exception e) {
                System.err.println(
                    "Failed to connect to the server: " + e.getMessage()
                );
                return;
            }

            // Authentication loop
            while (!authenticated) {
                displayAuthMenu();
                int option = getUserInput(scanner);

                switch (option) {
                    case 1:
                        handleRegister(scanner);
                        break;
                    case 2:
                        if (handleLogin(scanner)) {
                            authenticated = true;
                        }
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        timeSeriesClient.disconnect();
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            }

            // Main menu loop (after successful authentication)
            while (authenticated) {
                displayMainMenu();
                int option = getUserInput(scanner);

                switch (option) {
                    case 1:
                        handleAddEvent(scanner);
                        break;
                    case 2:
                        handleNextDay();
                        break;
                    case 3:
                        handleGetQuantitySales(scanner);
                        break;
                    case 4:
                        handleGetVolumeSales(scanner);
                        break;
                    case 5:
                        handleGetPriceStatistics(scanner);
                        break;
                    case 6: {
                        System.out.println("Logging out...");
                        authenticated = false;
                        timeSeriesClient.disconnect();
                        return;
                    }
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
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
                System.out.println(
                    "Registration successful! You can now login."
                );
            } else {
                System.out.println(
                    "Registration failed. User may already exist."
                );
            }
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
        }
    }

    private boolean handleLogin(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.next();
        System.out.print("Enter password: ");
        String password = scanner.next();

        try {
            if (timeSeriesClient.login(username, password)) {
                System.out.println(
                    "Login successful! Welcome, " + username + "!"
                );
                return true;
            } else {
                System.out.println("Login failed. Invalid credentials.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    private void handleAddEvent(Scanner scanner) {
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
        try {
            timeSeriesClient.nextDay();
            System.out.println("Advanced to next day successfully!");
        } catch (Exception e) {
            System.err.println(
                "Error advancing to next day: " + e.getMessage()
            );
        }
    }

    private void handleGetQuantitySales(Scanner scanner) {
        System.out.print("Enter product name: ");
        String productName = scanner.next();
        System.out.print("Enter number of days to look back: ");
        int daysLookback = scanner.nextInt();
        try {
            long quantity = timeSeriesClient.getQuantity(
                productName,
                daysLookback
            );
            System.out.println(
                "Total quantity sold in last " +
                    daysLookback +
                    " days: " +
                    quantity
            );
        } catch (Exception e) {
            System.err.println(
                "Error getting quantity of sales: " + e.getMessage()
            );
        }
    }

    private void handleGetVolumeSales(Scanner scanner) {
        System.out.print("Enter product name: ");
        String productName = scanner.next();
        System.out.print("Enter number of days to look back: ");
        int daysLookback = scanner.nextInt();
        try {
            double volume = timeSeriesClient.getVolume(
                productName,
                daysLookback
            );
            System.out.println(
                "Total volume sold in last " + daysLookback + " days: " + volume
            );
        } catch (Exception e) {
            System.err.println(
                "Error getting volume of sales: " + e.getMessage()
            );
        }
    }

    private void handleGetPriceStatistics(Scanner scanner) {
        System.out.print("Enter product name: ");
        String productName = scanner.next();
        System.out.print("Enter number of days to look back: ");
        int daysLookback = scanner.nextInt();
        try {
            TimeSeriesClient.PriceStats priceStats =
                timeSeriesClient.getPriceStats(productName, daysLookback);
            System.out.println(
                "Price statistics for " +
                    productName +
                    " in last " +
                    daysLookback +
                    " days:"
            );
            System.out.println("Average price: " + priceStats.average);
            System.out.println("Maximum price: " + priceStats.maximum);
        } catch (InputMismatchException e) {
            System.err.println("Invalid input: " + e.getMessage());
        } catch (IOException e) {
            System.err.println(
                "I/O error getting price statistics: " + e.getMessage()
            );
        } catch (RuntimeException e) {
            System.err.println(
                "Error getting price statistics: " + e.getMessage()
            );
        }
    }
}
