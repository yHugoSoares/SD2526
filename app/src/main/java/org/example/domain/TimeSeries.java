package org.example.domain;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Série temporal para um dia específico com sincronização eficiente
 * Usa ReadWriteLock para permitir múltiplas leituras simultâneas
 */
public class TimeSeries {
    private final List<Event> events;
    private final int day;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Cache de agregações
    private final Map<String, AggregationResult.QuantityResult> quantityCache = new HashMap<>();
    private final Map<String, AggregationResult.VolumeResult> volumeCache = new HashMap<>();
    private final Map<String, AggregationResult.PriceStats> priceStatsCache = new HashMap<>();
    
    public TimeSeries(int day) {
        this.events = new ArrayList<>();
        this.day = day;
    }
    
    /**
     * Adiciona evento à série temporal
     * Usa write lock pois modifica a lista
     */
    public void addEvent(Event event) {
        lock.writeLock().lock();
        try {
            events.add(event);
            // Invalidar caches relevantes
            quantityCache.remove(event.getProductName());
            volumeCache.remove(event.getProductName());
            priceStatsCache.remove(event.getProductName());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Obtém lista de eventos para produtos específicos
     * Usa read lock pois apenas lê
     */
    public List<Event> getEventsForProducts(Set<String> productNames) {
        lock.readLock().lock();
        try {
            List<Event> result = new ArrayList<>();
            for (Event event : events) {
                if (productNames.contains(event.getProductName())) {
                    result.add(event);
                }
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Calcula quantidade de vendas com caching lazy
     */
    public long calculateQuantity(String product, int daysLookback, int currentDay) {
        lock.readLock().lock();
        try {
            // Verificar cache
            AggregationResult.QuantityResult cached = quantityCache.get(product);
            if (cached != null && cached.computedAtDay == currentDay) {
                return cached.totalQuantity;
            }
        } finally {
            lock.readLock().unlock();
        }
        
        // Calcular e cachear
        lock.writeLock().lock();
        try {
            long total = 0;
            for (Event event : events) {
                if (event.getProductName().equals(product)) {
                    total += event.getQuantity();
                }
            }
            AggregationResult.QuantityResult result = new AggregationResult.QuantityResult(total, currentDay);
            quantityCache.put(product, result);
            return total;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Calcula volume de vendas com caching lazy
     */
    public double calculateVolume(String product, int daysLookback, int currentDay) {
        lock.readLock().lock();
        try {
            AggregationResult.VolumeResult cached = volumeCache.get(product);
            if (cached != null && cached.computedAtDay == currentDay) {
                return cached.totalVolume;
            }
        } finally {
            lock.readLock().unlock();
        }
        
        lock.writeLock().lock();
        try {
            double total = 0;
            for (Event event : events) {
                if (event.getProductName().equals(product)) {
                    total += event.getPrice() * event.getQuantity();
                }
            }
            AggregationResult.VolumeResult result = new AggregationResult.VolumeResult(total, currentDay);
            volumeCache.put(product, result);
            return total;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Calcula estatísticas de preço (média e máximo)
     */
    public AggregationResult.PriceStats calculatePriceStats(String product, int daysLookback, int currentDay) {
        lock.readLock().lock();
        try {
            AggregationResult.PriceStats cached = priceStatsCache.get(product);
            if (cached != null && cached.computedAtDay == currentDay) {
                return cached;
            }
        } finally {
            lock.readLock().unlock();
        }
        
        lock.writeLock().lock();
        try {
            double sum = 0;
            double max = Double.MIN_VALUE;
            long count = 0;
            
            for (Event event : events) {
                if (event.getProductName().equals(product)) {
                    sum += event.getPrice();
                    max = Math.max(max, event.getPrice());
                    count++;
                }
            }
            
            double average = count > 0 ? sum / count : 0;
            AggregationResult.PriceStats result = new AggregationResult.PriceStats(average, max, currentDay);
            priceStatsCache.put(product, result);
            return result;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Retorna cópia da lista de eventos (para processamento lazy)
     */
    public List<Event> getAllEvents() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(events);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public int getDay() { return day; }
    
    public void clearCache() {
        lock.writeLock().lock();
        try {
            quantityCache.clear();
            volumeCache.clear();
            priceStatsCache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
