package org.example.Client;

import java.net.Socket;
import java.io.*;
import java.util.Scanner;

public class Client {
    
    public static void main(String[] args) {
        
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedreader = null;
        BufferedWriter bufferedwriter = null;

        try {
            socket = new Socket("localhost", 12345);

            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedreader = new BufferedReader(inputStreamReader);
            bufferedwriter = new BufferedWriter(outputStreamWriter);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                String msgToSend = scanner.nextLine();
                bufferedwriter.write(msgToSend);
                bufferedwriter.newLine();
                bufferedwriter.flush();

                System.out.println("Server: " + bufferedreader.readLine());

                if (msgToSend.equalsIgnoreCase("exit") || msgToSend.equalsIgnoreCase("quit") || msgToSend.equalsIgnoreCase("bye")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedreader != null) bufferedreader.close();
                if (bufferedwriter != null) bufferedwriter.close();
                if (inputStreamReader != null) inputStreamReader.close();
                if (outputStreamWriter != null) outputStreamWriter.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
