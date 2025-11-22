package org.example;

import java.net.*;
import java.io.*;


public class Server {    
    public static void main(String[] args) {
        // Use try-with-resources so ServerSocket is always closed and handle IOException
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server listening on port 12345");

            while (true) {
                // Accept returns a Socket; use try-with-resources to close per-connection resources
                try (Socket socket = serverSocket.accept();
                     BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
                ) {
                    String msgFromClient;
                    while ((msgFromClient = bufferedreader.readLine()) != null) {
                        System.out.println("Client: " + msgFromClient);

                        String msgToSend = "Message received: " + msgFromClient;
                        bufferedwriter.write(msgToSend);
                        bufferedwriter.newLine();
                        bufferedwriter.flush();

                        if (msgFromClient.equalsIgnoreCase("exit")
                            || msgFromClient.equalsIgnoreCase("quit")
                            || msgFromClient.equalsIgnoreCase("bye")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    // Log and continue accepting other connections
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
