package client;

public class RegisterUser {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 2345; // match server port
        String username = "newuser";
        String password = "pass";

        if (args.length >= 1) username = args[0];
        if (args.length >= 2) password = args[1];
        if (args.length >= 3) port = Integer.parseInt(args[2]);

        TimeSeriesClient client = new TimeSeriesClient(host, port);
        client.connect();
        boolean ok = client.register(username, password);
        System.out.println("Register result: " + ok);
        client.disconnect();
    }
}