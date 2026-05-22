import java.util.*;

public class Graph {

    private Map<Integer, Vertex> vertexMap;
    private Map<Integer, List<Edge>> adjList;

    public Graph() {
        vertexMap = new HashMap<>();
        adjList = new HashMap<>();
    }

    public void addVertex(Vertex v) {
        vertexMap.put(v.getId(), v);
        adjList.put(v.getId(), new ArrayList<>());
    }

    public void addEdge(int from, int to, int weight) {
        if (!vertexMap.containsKey(from) || !vertexMap.containsKey(to)) {
            throw new IllegalArgumentException("Both vertices must exist. Missing: from=" + from + " to=" + to);
        }
        adjList.get(from).add(new Edge(vertexMap.get(from), vertexMap.get(to), weight));
        adjList.get(to).add(new Edge(vertexMap.get(to), vertexMap.get(from), weight));
    }

    public void printGraph() {
        System.out.println("── Weighted Adjacency List ─────────────────────");
        List<Integer> ids = new ArrayList<>(adjList.keySet());
        Collections.sort(ids);
        for (int id : ids) {
            System.out.print("  V(" + id + ") → ");
            List<Edge> edges = adjList.get(id);
            List<String> parts = new ArrayList<>();
            for (Edge e : edges) {
                parts.add("V(" + e.getDestination().getId() + ")[w=" + e.getWeight() + "]");
            }
            System.out.println(parts);
        }
        System.out.println("────────────────────────────────────────────────");
    }

    public List<Integer> bfs(int startId) {
        if (!vertexMap.containsKey(startId)) {
            throw new IllegalArgumentException("Start vertex " + startId + " not found.");
        }

        List<Integer> order = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.add(startId);

        while (!queue.isEmpty()) {
            int currentId = queue.poll();

            if (visited.contains(currentId)) {
                continue;
            }

            visited.add(currentId);
            order.add(currentId);

            for (Edge edge : adjList.get(currentId)) {
                int neighbourId = edge.getDestination().getId();
                if (!visited.contains(neighbourId)) {
                    queue.add(neighbourId);
                }
            }
        }

        return order;
    }

    public List<Integer> dfs(int startId) {
        if (!vertexMap.containsKey(startId)) {
            throw new IllegalArgumentException("Start vertex " + startId + " not found.");
        }

        List<Integer> order = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();

        stack.push(startId);

        while (!stack.isEmpty()) {
            int currentId = stack.pop();

            if (visited.contains(currentId)) {
                continue;
            }

            visited.add(currentId);
            order.add(currentId);

            List<Edge> edges = adjList.get(currentId);
            for (int i = edges.size() - 1; i >= 0; i--) {
                int neighbourId = edges.get(i).getDestination().getId();
                if (!visited.contains(neighbourId)) {
                    stack.push(neighbourId);
                }
            }
        }

        return order;
    }

    public void dijkstra(int startId) {
        if (!vertexMap.containsKey(startId)) {
            throw new IllegalArgumentException("Start vertex " + startId + " not found.");
        }

        int n = vertexMap.size();

        List<Integer> ids = new ArrayList<>(vertexMap.keySet());
        Collections.sort(ids);

        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        int[] prev = new int[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);

        dist[startId] = 0;

        for (int step = 0; step < n; step++) {

            int u = -1;
            for (int id : ids) {
                if (!visited[id] && (u == -1 || dist[id] < dist[u])) {
                    u = id;
                }
            }

            if (u == -1 || dist[u] == Integer.MAX_VALUE) {
                break;
            }

            visited[u] = true;

            for (Edge edge : adjList.get(u)) {
                int v = edge.getDestination().getId();
                int w = edge.getWeight();

                if (!visited[v] && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                }
            }
        }

        printDijkstraResults(startId, dist, prev, ids);
    }

    private void printDijkstraResults(int startId, int[] dist, int[] prev, List<Integer> ids) {
        System.out.println("── Dijkstra from V(" + startId + ") ─────────────────────");
        System.out.printf("  %-10s %-12s %s%n", "Vertex", "Distance", "Path");
        System.out.println("  ──────────────────────────────────────────────");
        for (int id : ids) {
            String distStr = (dist[id] == Integer.MAX_VALUE) ? "unreachable" : String.valueOf(dist[id]);
            String path = buildPath(startId, id, prev);
            System.out.printf("  %-10s %-12s %s%n", "V(" + id + ")", distStr, path);
        }
        System.out.println("────────────────────────────────────────────────");
    }

    private String buildPath(int startId, int targetId, int[] prev) {
        if (prev[targetId] == -1 && targetId != startId) {
            return "unreachable";
        }
        List<String> path = new ArrayList<>();
        int current = targetId;
        while (current != -1) {
            path.add(0, "V(" + current + ")");
            current = prev[current];
        }
        return String.join(" → ", path);
    }

    public int vertexCount() {
        return vertexMap.size();
    }
}