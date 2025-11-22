package org.example.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class TimeSeriesClient {
    private final String host;
    private final int port;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private final ReentrantLock socketLock = new ReentrantLock();

    public TimeSeriesClient(String host, int port) {
        this.host = host;
        this.port = port;
    }



}
