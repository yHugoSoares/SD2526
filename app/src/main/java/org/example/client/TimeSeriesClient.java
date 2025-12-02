package org.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.example.protocol.ProtocolCommands;

/**
 * Biblioteca cliente para acesso ao servidor de séries temporais
 * Suporta operações multi-threaded com sincronização eficiente
 */
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
    
    /**
     * Conecta ao servidor
     */
    public void connect() throws IOException {
        socketLock.lock();
        try {
            socket = new Socket(host, port);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Registra novo utilizador
     */
    public boolean register(String username, String password) throws IOException {
        socketLock.lock();
        try {
            dos.writeInt(ProtocolCommands.REGISTER);
            dos.writeUTF(username);
            dos.writeUTF(password);
            dos.flush();
            
            int response = dis.readInt();
            String message = dis.readUTF();
            return response == ProtocolCommands.RESPONSE_SUCCESS;
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Faz login no servidor
     */
    public boolean login(String username, String password) throws IOException {
        socketLock.lock();
        try {
            dos.writeInt(ProtocolCommands.LOGIN);
            dos.writeUTF(username);
            dos.writeUTF(password);
            dos.flush();
            
            int response = dis.readInt();
            String message = dis.readUTF();
            return response == ProtocolCommands.RESPONSE_SUCCESS;
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Adiciona evento ao dia corrente
     */
    public void addEvent(String productName, long quantity, double price) throws IOException {
        socketLock.lock();
        try {
            dos.writeInt(ProtocolCommands.ADD_EVENT);
            dos.writeUTF(productName);
            dos.writeLong(quantity);
            dos.writeDouble(price);
            dos.flush();
            
            int response = dis.readInt();
            String message = dis.readUTF();
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Avança para próximo dia
     */
    public int nextDay() throws IOException {
        socketLock.lock();
        try {
            dos.writeInt(ProtocolCommands.NEXT_DAY);
            dos.flush();
            
            int response = dis.readInt();
            int currentDay = dis.readInt();
            return currentDay;
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Obtém quantidade de vendas nos últimos d dias
     */
    public long getQuantity(String product, int daysLookback) throws IOException {
        socketLock.lock();
        try {
            dos.writeInt(ProtocolCommands.GET_QUANTITY);
            dos.writeUTF(product);
            dos.writeInt(daysLookback);
            dos.flush();
            
            int response = dis.readInt();
            return dis.readLong();
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Obtém volume de vendas nos últimos d dias
     */
    public double getVolume(String product, int daysLookback) throws IOException {
        socketLock.lock();
        try {
            dos.writeInt(ProtocolCommands.GET_VOLUME);
            dos.writeUTF(product);
            dos.writeInt(daysLookback);
            dos.flush();
            
            int response = dis.readInt();
            return dis.readDouble();
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Obtém estatísticas de preço (média e máximo)
     */
    public PriceStats getPriceStats(String product, int daysLookback) throws IOException {
        socketLock.lock();
        try {
            dos.writeInt(ProtocolCommands.GET_PRICE_STATS);
            dos.writeUTF(product);
            
            dos.writeInt(daysLookback);
            dos.flush();
            
            int response = dis.readInt();
            double average = dis.readDouble();
            double maximum = dis.readDouble();
            return new PriceStats(average, maximum);
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Obtém eventos para produtos específicos de um dia anterior
     */
    public List<EventRecord> getEvents(int dayOffset, Set<String> products) throws IOException {
        socketLock.lock();
        try {
            dos.writeInt(ProtocolCommands.GET_EVENTS);
            dos.writeInt(dayOffset);
            dos.writeInt(products.size());
            for (String product : products) {
                dos.writeUTF(product);
            }
            dos.flush();
            
            int response = dis.readInt();
            List<EventRecord> events = new ArrayList<>();
            int eventCount = dis.readInt();
            
            for (int i = 0; i < eventCount; i++) {
                String productName = dis.readUTF();
                long quantity = dis.readLong();
                double price = dis.readDouble();
                events.add(new EventRecord(productName, quantity, price));
            }
            
            return events;
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Espera por vendas simultâneas de dois produtos
     */
    public boolean waitSimultaneous(String product1, String product2, long timeoutMs) throws IOException {
        socketLock.lock();
        try {
            dos.writeInt(ProtocolCommands.WAIT_SIMULTANEOUS);
            dos.writeUTF(product1);
            dos.writeUTF(product2);
            dos.flush();
            
            int response = dis.readInt();
            return dis.readBoolean();
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Espera por n vendas consecutivas do mesmo produto
     */
    public String waitConsecutive(int count, long timeoutMs) throws IOException {
        socketLock.lock();
        try {
            dos.writeInt(ProtocolCommands.WAIT_CONSECUTIVE);
            dos.writeInt(count);
            dos.flush();
            
            int response = dis.readInt();
            return dis.readUTF();
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Fecha conexão com servidor
     */
    public void disconnect() throws IOException {
        socketLock.lock();
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } finally {
            socketLock.unlock();
        }
    }
    
    /**
     * Classe auxiliar para armazenar informações de eventos
     */
    public static class EventRecord {
        public String productName;
        public long quantity;
        public double price;
        
        public EventRecord(String productName, long quantity, double price) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }
    }
    
    /**
     * Classe auxiliar para armazenar estatísticas de preço
     */
    public static class PriceStats {
        public double average;
        public double maximum;
        
        public PriceStats(double average, double maximum) {
            this.average = average;
            this.maximum = maximum;
        }
    }
}
