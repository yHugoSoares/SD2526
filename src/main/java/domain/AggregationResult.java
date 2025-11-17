package domain;

/**
 * Cache para resultados de agregações
 */
public class AggregationResult {
    public static class QuantityResult {
        public long totalQuantity;
        public long computedAtDay;
        
        public QuantityResult(long totalQuantity, long computedAtDay) {
            this.totalQuantity = totalQuantity;
            this.computedAtDay = computedAtDay;
        }
    }
    
    public static class VolumeResult {
        public double totalVolume;
        public long computedAtDay;
        
        public VolumeResult(double totalVolume, long computedAtDay) {
            this.totalVolume = totalVolume;
            this.computedAtDay = computedAtDay;
        }
    }
    
    public static class PriceStats {
        public double average;
        public double maximum;
        public long computedAtDay;
        
        public PriceStats(double average, double maximum, long computedAtDay) {
            this.average = average;
            this.maximum = maximum;
            this.computedAtDay = computedAtDay;
        }
    }
}
