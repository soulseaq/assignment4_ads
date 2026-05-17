import java.util.*;

public class Graph {

    private Map<Integer, Vertex> vertexMap;
    private Map<Integer, List<Vertex>> adjList;

    public Graph() {
        vertexMap = new HashMap<>();
        adjList = new HashMap<>();
    }

    public void addVertex(Vertex v) {
        vertexMap.put(v.getId(), v);
        adjList.put(v.getId(), new ArrayList<>());
    }

    public void addEdge(int from, int to) {
        if (!vertexMap.containsKey(from) || !vertexMap.containsKey(to)) {
            throw new IllegalArgumentException("Both vertices must exist. Missing: from=" + from + " to=" + to);
        }
        adjList.get(from).add(vertexMap.get(to));
        adjList.get(to).add(vertexMap.get(from));
    }

    public void printGraph() {
        System.out.println("── Adjacency List ──────────────────────────────");
        List<Integer> ids = new ArrayList<>(adjList.keySet());
        Collections.sort(ids);
        for (int id : ids) {
            System.out.println("  " + vertexMap.get(id) + " → " + adjList.get(id));
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

            for (Vertex neighbour : adjList.get(currentId)) {
                if (!visited.contains(neighbour.getId())) {
                    queue.add(neighbour.getId());
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

            List<Vertex> neighbours = adjList.get(currentId);
            for (int i = neighbours.size() - 1; i >= 0; i--) {
                Vertex neighbour = neighbours.get(i);
                if (!visited.contains(neighbour.getId())) {
                    stack.push(neighbour.getId());
                }
            }
        }

        return order;
    }

    public int vertexCount() {
        return vertexMap.size();
    }
}