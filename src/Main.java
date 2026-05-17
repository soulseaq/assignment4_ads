public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Assignment 4 – Graph Traversal & Representation    ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println("  Algorithms: BFS (Breadth-First) | DFS (Depth-First)  ");
        System.out.println("  Graph type: Undirected, connected ring + shortcut     ");
        System.out.println("  Sizes tested: 10, 30, 100 vertices                   ");

        Experiment experiment = new Experiment();
        experiment.runMultipleTests();

        System.out.println("\n✓ All experiments completed.");
    }
}