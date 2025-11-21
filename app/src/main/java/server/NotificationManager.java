package server;

import java.util.*;
import java.util.concurrent.locks.*;

/**
 * Gerencia notificações de vendas simultâneas e consecutivas
 * Usa Condition Variables para eficiência (threads acordadas apenas quando necessário)
 */
public class NotificationManager {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition eventOccurred = lock.newCondition();
    
    // Rastreamento de eventos do dia corrente
    private final Set<String> productsWithSales = new HashSet<>();
    private final Map<String, Integer> consecutiveCount = new HashMap<>();
    private String lastProduct = null;
    
    /**
     * Registra venda de um produto
     */
    public void recordSale(String productName) {
        lock.lock();
        try {
            productsWithSales.add(productName);
            
            // Atualizar contador de vendas consecutivas
            if (productName.equals(lastProduct)) {
                consecutiveCount.put(productName, consecutiveCount.getOrDefault(productName, 0) + 1);
            } else {
                consecutiveCount.put(productName, 1);
                lastProduct = productName;
            }
            
            // Acordar todas as threads em espera
            eventOccurred.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Espera por vendas simultâneas de dois produtos
     * Bloqueada até ambos os produtos terem vendas no dia ou até o dia acabar
     */
    public boolean waitForSimultaneousSales(String product1, String product2, long timeoutMs, int currentDay) {
        lock.lock();
        try {
            long deadline = System.currentTimeMillis() + timeoutMs;
            
            while (!productsWithSales.contains(product1) || !productsWithSales.contains(product2)) {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    return false; // Timeout
                }
                
                // Wait com timeout
                if (!eventOccurred.await(remaining, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                    return false;
                }
            }
            
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Espera por n vendas consecutivas do mesmo produto
     */
    public String waitForConsecutiveSales(int count, long timeoutMs) {
        lock.lock();
        try {
            long deadline = System.currentTimeMillis() + timeoutMs;
            
            while (true) {
                // Verificar se algum produto tem o número de vendas consecutivas desejado
                for (Map.Entry<String, Integer> entry : consecutiveCount.entrySet()) {
                    if (entry.getValue() >= count) {
                        return entry.getKey();
                    }
                }
                
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    return null; // Timeout
                }
                
                if (!eventOccurred.await(remaining, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                    return null;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Reset para novo dia
     */
    public void reset() {
        lock.lock();
        try {
            productsWithSales.clear();
            consecutiveCount.clear();
            lastProduct = null;
        } finally {
            lock.unlock();
        }
    }
}
