public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║     Assignment 4 Bonus — Dijkstra's Algorithm        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        Graph g = new Graph();

        for (int i = 0; i < 6; i++) {
            g.addVertex(new Vertex(i));
        }

        g.addEdge(0, 1, 4);
        g.addEdge(0, 2, 1);
        g.addEdge(2, 1, 2);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 4, 5);
        g.addEdge(3, 4, 3);
        g.addEdge(3, 5, 6);
        g.addEdge(4, 5, 2);

        System.out.println();
        g.printGraph();

        System.out.println("\nBFS from V(0): " + g.bfs(0));
        System.out.println("DFS from V(0): " + g.dfs(0));

        System.out.println();
        g.dijkstra(0);

        System.out.println("\n✓ Bonus task completed.");
    }
}