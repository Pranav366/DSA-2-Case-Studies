import java.util.List;
import java.util.ArrayList;

class UnionFind {
    private int[] parent;
    private int[] rank;
    private int[] size;
    private int totalComponents;
    private int maxSize;

    // 1. Initialization
    public UnionFind(int numChannels) {
        parent = new int[numChannels];
        rank = new int[numChannels];
        size = new int[numChannels];
        totalComponents = numChannels;
        maxSize = 1;

        for (int i = 0; i < numChannels; i++) {
            parent[i] = i;
            size[i] = 1;
            rank[i] = 0;
        }
    }

    // 3. Find with Path Compression
    public int find(int i) {
        if (parent[i] != i) {
            parent[i] = find(parent[i]); // Path compression
        }
        return parent[i];
    }

    // 4. Union by Rank
    public void union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);

        if (rootI != rootJ) {
            // Attach smaller rank tree under larger rank tree
            if (rank[rootI] < rank[rootJ]) {
                int temp = rootI;
                rootI = rootJ;
                rootJ = temp;
            }

            parent[rootJ] = rootI;
            if (rank[rootI] == rank[rootJ]) {
                rank[rootI]++;
            }

            // 6 & 8. Merge Sizes and Update Max Size
            size[rootI] += size[rootJ];
            if (size[rootI] > maxSize) {
                maxSize = size[rootI];
            }

            // 7. Update Component Count
            totalComponents--;
        }
    }

    public int getTotalComponents() {
        return totalComponents;
    }

    public int getMaxSize() {
        return maxSize;
    }
}

public class SlackConnectivity {
    
    // 5. Processing Memberships
    public static int[] solveSlackConnectivity(int numChannels, List<List<Integer>> userMemberships) {
        UnionFind uf = new UnionFind(numChannels);

        for (List<Integer> channels : userMemberships) {
            if (channels.size() > 1) {
                int baseChannel = channels.get(0);
                for (int i = 1; i < channels.size(); i++) {
                    uf.union(baseChannel, channels.get(i));
                }
            }
        }

        // 10. Final Answer
        return new int[]{uf.getTotalComponents(), uf.getMaxSize()};
    }

    public static void main(String[] args) {
        int totalChannels = 12000;

        // Mock data: List of channels each user belongs to
        List<List<Integer>> mockMemberships = new ArrayList<>();
        
        // User A is in channels 0, 1, 2
        mockMemberships.add(List.of(0, 1, 2));
        // User B is in channels 2, 3
        mockMemberships.add(List.of(2, 3));
        // User C is in channels 4, 5
        mockMemberships.add(List.of(4, 5));

        int[] result = solveSlackConnectivity(totalChannels, mockMemberships);

        System.out.println("Total Connected Components: " + result[0]);
        System.out.println("Size of the Largest Component: " + result[1]);
    }
}