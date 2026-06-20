import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class BloombergSymbolIndex {

    // Flat parallel arrays to maximize CPU cache locality and eliminate pointer-chasing overhead
    private long[] encodedTickers;
    private int[] instrumentIds;
    private int size;

    /**
     * Packs an ASCII string of up to 8 characters into a single 64-bit primitive long.
     * This turns string comparisons into $O(1)$ hardware-level integer comparisons.
     */
    private static long packTicker(String ticker) {
        byte[] bytes = ticker.getBytes(StandardCharsets.US_ASCII);
        long packed = 0;
        int length = Math.min(bytes.length, 8);
        for (int i = 0; i < length; i++) {
            packed |= ((long) (bytes[i] & 0xFF)) << (i * 8);
        }
        return packed;
    }

    /**
     * Load Phase: Called once at market open. 
     * Expects data to be pre-sorted alphabetically.
     */
    public void loadMarketOpen(List<TickerData> sortedFeed) {
        this.size = sortedFeed.size();
        this.encodedTickers = new long[size];
        this.instrumentIds = new int[size];

        for (int i = 0; i < size; i++) {
            TickerData data = sortedFeed.get(i);
            this.encodedTickers[i] = packTicker(data.ticker);
            this.instrumentIds[i] = data.instrumentId;
        }
    }

    /**
     * Branchless Binary Search Lookup: Optimized for 50,000 requests/sec.
     * Uses arithmetic masks to avoid JVM branch mispredictions.
     */
    public int lookup(String targetTicker) {
        long target = packTicker(targetTicker);
        int base = 0;
        int n = size;

        // Loop execution is entirely deterministic in length (approx 14 iterations for 12,000 elements)
        while (n > 1) {
            int half = n / 2;
            // Branchless execution: If true, flag is 1; if false, flag is 0
            int flag = (encodedTickers[base + half] <= target) ? 1 : 0;
            base += flag * half;
            n -= half;
        }

        // Final validation check
        if (encodedTickers[base] == target) {
            return instrumentIds[base];
        }
        return -1; // Ticker not found
    }

    // Simple POJO container used only during initialization
    public static class TickerData {
        String ticker;
        int instrumentId;

        public TickerData(String ticker, int instrumentId) {
            this.ticker = ticker;
            this.instrumentId = instrumentId;
        }
    }

    // Execution / Micro-benchmark Demo
    public static void main(String[] args) {
        // 1. Mocking the pre-sorted market open feed
        List<TickerData> marketOpenFeed = Arrays.asList(
            new TickerData("AAPL", 1001), new TickerData("ADBE", 1002), 
            new TickerData("AMZN", 1003), new TickerData("BABA", 1004), 
            new TickerData("BKNG", 1005), new TickerData("COST", 1006), 
            new TickerData("GOOGL", 1007), new TickerData("JPM", 1008), 
            new TickerData("META", 1009), new TickerData("MSFT", 1010), 
            new TickerData("NVDA", 1011), new TickerData("ORCL", 1012), 
            new TickerData("TSLA", 1013)
        );

        BloombergSymbolIndex index = new BloombergSymbolIndex();
        index.loadMarketOpen(marketOpenFeed);

        // 2. Lookup Profile
        String query = "GOOGL";
        
        long startTime = System.nanoTime();
        int resultId = index.lookup(query);
        long endTime = System.nanoTime();

        if (resultId != -1) {
            System.out.println("Ticker: " + query + " found! Instrument ID: " + resultId);
        } else {
            System.out.println("Ticker not found.");
        }
        
        System.out.println("Java Lookup Latency: " + (endTime - startTime) + " ns");
    }
}