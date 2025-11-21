package client;

import java.util.*;
import java.io.IOException;

/**
 * Simple interactive console that talks to the server using TimeSeriesClient.
 */
public class ClientConsole {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 2345;
        TimeSeriesClient client = new TimeSeriesClient(host, port);
        Scanner scanner = new Scanner(System.in);

        try {
            client.connect();
            System.out.println("Connected to " + host + ":" + port);
            printHelp();

            boolean running = true;
            while (running) {
                System.out.print("> ");
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+", 2);
                String cmd = parts[0].toLowerCase();
                String arg = parts.length > 1 ? parts[1] : "";

                try {
                    switch (cmd) {
                        case "help": printHelp(); break;
                        case "quit":
                        case "exit":
                            running = false;
                            break;
                        case "register": {
                            String[] a = arg.split("\\s+");
                            if (a.length < 2) { System.out.println("Usage: register <user> <pass>"); break; }
                            boolean ok = client.register(a[0], a[1]);
                            System.out.println("Register: " + ok);
                            break;
                        }
                        case "login": {
                            String[] a = arg.split("\\s+");
                            if (a.length < 2) { System.out.println("Usage: login <user> <pass>"); break; }
                            boolean ok = client.login(a[0], a[1]);
                            System.out.println("Login: " + ok);
                            break;
                        }
                        case "add": { // add <product> <quantity> <price>
                            String[] a = arg.split("\\s+");
                            if (a.length < 3) { System.out.println("Usage: add <product> <quantity> <price>"); break; }
                            client.addEvent(a[0], Long.parseLong(a[1]), Double.parseDouble(a[2]));
                            System.out.println("Event added.");
                            break;
                        }
                        case "next": {
                            int day = client.nextDay();
                            System.out.println("Advanced to day " + day);
                            break;
                        }
                        case "qty": { // qty <product> <days>
                            String[] a = arg.split("\\s+");
                            if (a.length < 2) { System.out.println("Usage: qty <product> <days>"); break; }
                            long q = client.getQuantity(a[0], Integer.parseInt(a[1]));
                            System.out.println("Quantity: " + q);
                            break;
                        }
                        case "vol": { // vol <product> <days>
                            String[] a = arg.split("\\s+");
                            if (a.length < 2) { System.out.println("Usage: vol <product> <days>"); break; }
                            double v = client.getVolume(a[0], Integer.parseInt(a[1]));
                            System.out.println("Volume: " + v);
                            break;
                        }
                        case "stats": { // stats <product> <days>
                            String[] a = arg.split("\\s+");
                            if (a.length < 2) { System.out.println("Usage: stats <product> <days>"); break; }
                            TimeSeriesClient.PriceStats s = client.getPriceStats(a[0], Integer.parseInt(a[1]));
                            System.out.printf("Avg=%.2f Max=%.2f%n", s.average, s.maximum);
                            break;
                        }
                        case "events": { // events <dayOffset> <comma-separated-products>
                            String[] a = arg.split("\\s+", 2);
                            if (a.length < 2) { System.out.println("Usage: events <dayOffset> <prod1,prod2,...>"); break; }
                            int off = Integer.parseInt(a[0]);
                            Set<String> prods = new HashSet<>(Arrays.asList(a[1].split(",")));
                            List<TimeSeriesClient.EventRecord> evs = client.getEvents(off, prods);
                            for (TimeSeriesClient.EventRecord er : evs) {
                                System.out.printf("%s: %d @ %.2f%n", er.productName, er.quantity, er.price);
                            }
                            break;
                        }
                        case "waitsim": { // waitsim <prod1> <prod2>
                            String[] a = arg.split("\\s+");
                            if (a.length < 2) { System.out.println("Usage: waitsim <prod1> <prod2>"); break; }
                            boolean r = client.waitSimultaneous(a[0], a[1], 60000);
                            System.out.println("Simultaneous result: " + r);
                            break;
                        }
                        case "waitconsec": { // waitconsec <count>
                            int cnt = Integer.parseInt(arg.trim());
                            String prod = client.waitConsecutive(cnt, 60000);
                            System.out.println("Consecutive result: " + (prod == null ? "timeout" : prod));
                            break;
                        }
                        default:
                            System.out.println("Unknown command (type help)");
                    }
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } finally {
            try { client.disconnect(); } catch (Exception ignored) {}
            System.out.println("Disconnected.");
        }
    }

    private static void printHelp() {
        System.out.println("Commands:");
        System.out.println("  help");
        System.out.println("  register <user> <pass>");
        System.out.println("  login <user> <pass>");
        System.out.println("  add <product> <quantity> <price>");
        System.out.println("  next");
        System.out.println("  qty <product> <days>");
        System.out.println("  vol <product> <days>");
        System.out.println("  stats <product> <days>");
        System.out.println("  events <dayOffset> <prod1,prod2,...>");
        System.out.println("  waitsim <prod1> <prod2>");
        System.out.println("  waitconsec <count>");
        System.out.println("  quit");
    }
}