import java.util.*;

public class GoogleMapsReroute {

    // Class to represent a node and its cumulative weight in the Priority Queue
    static class PathNode implements Comparable<PathNode> {
        String name;
        double time;
        List<String> path;

        public PathNode(String name, double time, List<String> path) {
            this.name = name;
            this.time = time;
            this.path = new ArrayList<>(path);
            this.path.add(name);
        }

        @Override
        public int compareTo(PathNode other) {
            return Double.compare(this.time, other.time);
        }
    }

    public static Map<String, List<String>> dijkstra(Map<String, Map<String, Double>> graph, String start, String end) {
        PriorityQueue<PathNode> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();
        
        // Initialize Priority Queue with the start node
        pq.add(new PathNode(start, 0.0, new ArrayList<>()));

        while (!pq.isEmpty()) {
            PathNode current = pq.poll();

            if (visited.contains(current.name)) continue;
            visited.add(current.name);

            // Early Termination Check: If destination is reached
            if (current.name.equals(end)) {
                Map<String, List<String>> result = new HashMap<>();
                result.put(String.valueOf(current.time), current.path);
                return result;
            }

            Map<String, Double> neighbors = graph.getOrDefault(current.name, Collections.emptyMap());
            for (Map.Entry<String, Double> neighbor : neighbors.entrySet()) {
                if (!visited.contains(neighbor.getKey())) {
                    pq.add(new PathNode(neighbor.getKey(), current.time + neighbor.getValue(), current.path));
                }
            }
        }
        return Collections.emptyMap(); // Return empty if no path is found
    }

    public static void main(String[] args) {
        // 1. Graph Representation: Map of maps for the 11-edge Bangalore road network
        Map<String, Map<String, Double>> bangaloreGraph = new HashMap<>();
        
        // Helper to populate bidirectional edges easily
        addEdge(bangaloreGraph, "IND", "KOR", 15.0);
        addEdge(bangaloreGraph, "IND", "MGR", 10.0);
        addEdge(bangaloreGraph, "MGR", "KOR", 1