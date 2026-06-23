import java.util.Arrays;

public class DelhiveryTSP {

    // Using a large number to represent infinity for unreachable states
    private static final int INF = 100000000;

    public static void main(String[] args) {
        // Distance Matrix (Index 0 = Warehouse W, 1 = D1, 2 = D2, 3 = D3, 4 = D4)
        int[][] dist = {
            {0,  10, 15, 20, 12}, // W
            {10, 0,  35, 25, 30}, // D1
            {15, 35, 0,  30, 20}, // D2
            {20, 25, 30, 0,  15}, // D3
            {12, 30, 20, 15, 0}   // D4
        };

        int numCustomers = 4; // D1, D2, D3, D4
        int n = numCustomers + 1; // Total locations including Warehouse (index 0)

        // Number of customer subsets is 2^numCustomers = 16
        int numSubsets = 1 << numCustomers; 
        
        // dp[S][v]: S is the bitmask of visited customers, v is the last visited customer index (1 to 4)
        int[][] dp = new int[numSubsets][n];
        
        // Initialize DP table with infinity
        for (int[] row : dp) {
            Arrays.fill(row, INF);
        }

        // Base cases: Travelling from Warehouse (W) to the first customer D_i
        // Bitmask for just customer D_i is (1 << (i - 1))
        for (int i = 1; i <= numCustomers; i++) {
            dp[1 << (i - 1)][i] = dist[0][i];
        }

        // Iterate through all possible subset bitmasks
        for (int mask = 1; mask < numSubsets; mask++) {
            for (int v = 1; v <= numCustomers; v++) {
                // Check if customer v is actually in the current subset mask
                if ((mask & (1 << (v - 1))) == 0) continue;

                // Look for a preceding customer 'u' to transition from
                for (int u = 1; u <= numCustomers; u++) {
                    // u must be in the subset and cannot be the same as v
                    if (u == v || (mask & (1 << (u - 1))) == 0) continue;

                    // Mask without the current node v
                    int prevMask = mask ^ (1 << (v - 1)); 
                    
                    // State Transition
                    if (dp[prevMask][u] + dist[u][v] < dp[mask][v]) {
                        dp[mask][v] = dp[prevMask][u] + dist[u][v];
                    }
                }
            }
        }

        // Final Answer: Close the tour by connecting back to Warehouse (index 0)
        int fullSetMask = numSubsets - 1; // All bits set to 1 (binary 1111)
        int minTourCost = INF;

        for (int v = 1; v <= numCustomers; v++) {
            int totalCost = dp[fullSetMask][v] + dist[v][0];
            if (totalCost < minTourCost) {
                minTourCost = totalCost;
            }
        }

        System.out.println("The shortest closed-tour distance is: " + minTourCost + " km");
    }
}