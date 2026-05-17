import java.util.List;

public class Experiment {

    private String experimentLabel;
    private long bfsTimeNs;
    private long dfsTimeNs;

    public void runTraversals(Graph g, String label, boolean printOrder) {
        this.experimentLabel = label;

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("  Experiment: " + label);
        System.out.println("══════════════════════════════════════════════════");

        long bfsStart = System.nanoTime();
        List<Integer> bfsOrder = g.bfs(0);
        long bfsEnd = System.nanoTime();
        bfsTimeNs = bfsEnd - bfsStart;

        long dfsStart = System.nanoTime();
        List<Integer> dfsOrder = g.dfs(0);
        long dfsEnd = System.nanoTime();
        dfsTimeNs = dfsEnd - dfsStart;

        System.out.printf("  BFS time: %,d ns  (%,.1f µs)%n", bfsTimeNs, bfsTimeNs / 1_000.0);
        System.out.printf("  DFS time: %,d ns  (%,.1f µs)%n", dfsTimeNs, dfsTimeNs / 1_000.0);

        if (printOrder) {
            System.out.println("  BFS order: " + bfsOrder);
            System.out.println("  DFS order: " + dfsOrder);
        } else {
            System.out.println("  (Traversal order omitted — " + bfsOrder.size() + " vertices visited)");
        }
    }

    public void runMultipleTests() {
        int[] sizes = {10, 30, 100};
        Experiment[] experiments = new Experiment[sizes.length];

        for (int i = 0; i < sizes.length; i++) {
            int n = sizes[i];
            Graph graph = buildConnectedGraph(n);

            String label = switch (n) {
                case 10  -> "Small  (" + n + " vertices)";
                case 30  -> "Medium (" + n + " vertices)";
                default  -> "Large  (" + n + " vertices)";
            };

            boolean showOrder = (n == 10);

            if (showOrder) {
                System.out.println("\n── Graph structure (small) ─────────────────────");
                graph.printGraph();
            }

            experiments[i] = new Experiment();
            experiments[i].runTraversals(graph, label, showOrder);
        }

        printResults(experiments, sizes);
    }

    public void printResults(Experiment[] experiments, int[] sizes) {
        System.out.println("\n\n╔══════════════════════════════════════════════════════╗");
        System.out.println(  "║            PERFORMANCE COMPARISON TABLE              ║");
        System.out.println(  "╠══════════════╦══════════════════╦════════════════════╣");
        System.out.println(  "║   Graph Size ║   BFS Time (µs)  ║   DFS Time (µs)    ║");
        System.out.println(  "╠══════════════╬══════════════════╬════════════════════╣");

        for (int i = 0; i < experiments.length; i++) {
            System.out.printf("║  %4d vertices║  %14.2f  ║  %16.2f    ║%n",
                    sizes[i],
                    experiments[i].bfsTimeNs / 1_000.0,
                    experiments[i].dfsTimeNs / 1_000.0);
        }

        System.out.println("╚══════════════╩══════════════════╩════════════════════╝");
        System.out.println("\n  Note: times are single-run measurements; JVM warm-up");
        System.out.println("  may cause the first run to appear slower than later ones.");
    }

    private Graph buildConnectedGraph(int n) {
        Graph g = new Graph();

        for (int i = 0; i < n; i++) {
            g.addVertex(new Vertex(i));
        }

        for (int i = 0; i < n; i++) {
            g.addEdge(i, (i + 1) % n);
        }

        for (int i = 0; i < n; i++) {
            if (n > 4) g.addEdge(i, (i + 2) % n);
            if (n > 6) g.addEdge(i, (i + 3) % n);
        }

        return g;
    }
}