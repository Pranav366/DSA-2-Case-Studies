import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CricketDeliverySorter {

    // Delivery record payload
    public static class Delivery {
        int over;          // 0 to 49
        int ball;          // 1 to 12 (pathological cases included)
        String batsmanId;
        String bowlerId;
        int runs;
        String dismissalType;

        public Delivery(int over, int ball, String batsmanId, String bowlerId, int runs, String dismissalType) {
            this.over = over;
            this.ball = ball;
            this.batsmanId = batsmanId;
            this.bowlerId = bowlerId;
            this.runs = runs;
            this.dismissalType = dismissalType;
        }

        @Override
        public String toString() {
            return String.format("(Over: %d, Ball: %d, Bowler: %s, Runs: %d)", over, ball, bowlerId, runs);
        }
    }

    // Constants for flat-key mapping
    private static final int MAX_OVERS = 50; 
    private static final int MAX_BALLS_PER_OVER = 13; // 1 to 12 slots needed
    private static final int TOTAL_BUCKETS = MAX_OVERS * MAX_BALLS_PER_OVER;

    /**
     * Flattens the composite (over, ball) key into a unique, sequentially ordered integer.
     */
    private static int getFlatKey(int over, int ball) {
        return (over * MAX_BALLS_PER_OVER) + (ball - 1);
    }

    /**
     * Sorts deliveries in O(n) time using Counting Sort logic on the flattened key.
     */
    public static Delivery[] sortDeliveries(List<Delivery> deliveries) {
        int n = deliveries.size();
        Delivery[] sorted = new Delivery[n];
        int[] counts = new int[TOTAL_BUCKETS];

        // 1. Count occurrences of each unique (over, ball) key
        for (Delivery d : deliveries) {
            int key = getFlatKey(d.over, d.ball);
            counts[key]++;
        }

        // 2. Transform counts to prefix sums to find starting positions
        for (int i = 1; i < TOTAL_BUCKETS; i++) {
            counts[i] += counts[i - 1];
        }

        // 3. Build the output array in reverse order to maintain stability
        for (int i = n - 1; i >= 0; i--) {
            Delivery d = deliveries.get(i);
            int key = getFlatKey(d.over, d.ball);
            sorted[counts[key] - 1] = d;
            counts[key]--;
        }

        return sorted;
    }

    public static void main(String[] args) {
        // Simulating the 10-delivery example
        List<Delivery> unsortedMockData = new ArrayList<>(Arrays.asList(
            new Delivery(2, 4, "BatA", "BowlX", 1, "none"),
            new Delivery(1, 1, "BatB", "BowlY", 4, "none"),
            new Delivery(3, 6, "BatC", "BowlZ", 0, "bowled"),
            new Delivery(1, 5, "BatA", "BowlY", 6, "none"),
            new Delivery(2, 2, "BatD", "BowlX", 0, "none"),
            new Delivery(3, 1, "BatB", "BowlZ", 1, "none"),
            new Delivery(1, 3, "BatC", "BowlY", 2, "none"),
            new Delivery(2, 6, "BatA", "BowlX", 4, "none"),
            new Delivery(3, 4, "BatD", "BowlZ", 1, "none"),
            new Delivery(1, 2, "BatB", "BowlY", 0, "none")
        ));

        System.out.println("--- Unsorted Deliveries ---");
        unsortedMockData.forEach(System.out::println); // Fixed: changed . to ::

        // Perform the O(n) sort
        Delivery[] sortedData = sortDeliveries(unsortedMockData);

        System.out.println("\n--- Chronologically Sorted Deliveries ---");
        for (Delivery d : sortedData) {
            System.out.println(d);
        }
    }
}