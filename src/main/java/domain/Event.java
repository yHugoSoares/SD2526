package domain;

import java.io.Serializable;

/**
 * Representa um evento de venda na série temporal
 */
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String productName;
    private long quantity;
    private double price;
    private long timestamp;
    
    public Event(String productName, long quantity, double price, long timestamp) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
    }
    
    public String getProductName() { return productName; }
    public long getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public long getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("Event{product=%s, qty=%d, price=%.2f, ts=%d}", 
                            productName, quantity, price, timestamp);
    }
}
