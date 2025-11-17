package protocol;

import java.io.*;
import java.util.*;

/**
 * Protocolo binário eficiente com compressão de nomes de produtos
 * Implementa serialização compacta para minimizar tráfego de rede
 */
public class BinaryProtocol {
    /**
     * Serializa lista de eventos de forma compacta
     * Usa dicionário de nomes de produtos para evitar repetição
     */
    public static void serializeEvents(DataOutputStream dos, List<Object[]> events) throws IOException {
        // Mapa de nomes únicos com índices
        Map<String, Integer> productIndexMap = new HashMap<>();
        int nextIndex = 0;
        
        // Primeira passagem: coletar nomes únicos
        for (Object[] event : events) {
            String productName = (String) event[0];
            if (!productIndexMap.containsKey(productName)) {
                productIndexMap.put(productName, nextIndex++);
            }
        }
        
        // Escrever dicionário
        dos.writeInt(productIndexMap.size());
        for (Map.Entry<String, Integer> entry : productIndexMap.entrySet()) {
            dos.writeInt(entry.getValue());
            dos.writeUTF(entry.getKey());
        }
        
        // Escrever eventos usando índices
        dos.writeInt(events.size());
        for (Object[] event : events) {
            String productName = (String) event[0];
            int productIndex = productIndexMap.get(productName);
            dos.writeInt(productIndex);
            dos.writeLong((Long) event[1]);  // quantity
            dos.writeDouble((Double) event[2]); // price
        }
    }
    
    /**
     * Desserializa lista de eventos de forma compacta
     */
    public static List<Object[]> deserializeEvents(DataInputStream dis) throws IOException {
        List<Object[]> events = new ArrayList<>();
        
        // Ler dicionário
        Map<Integer, String> productNameMap = new HashMap<>();
        int dictSize = dis.readInt();
        for (int i = 0; i < dictSize; i++) {
            int index = dis.readInt();
            String name = dis.readUTF();
            productNameMap.put(index, name);
        }
        
        // Ler eventos
        int eventCount = dis.readInt();
        for (int i = 0; i < eventCount; i++) {
            int productIndex = dis.readInt();
            long quantity = dis.readLong();
            double price = dis.readDouble();
            
            String productName = productNameMap.get(productIndex);
            events.add(new Object[]{productName, quantity, price});
        }
        
        return events;
    }
}
