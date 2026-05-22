# Assignment 4 — Graph Traversal and Representation System

**Course:** Algorithms and Data Structures
**Language:** Java 17+
**Algorithms:** Breadth-First Search (BFS) | Depth-First Search (DFS) | Dijkstra's Algorithm (Bonus)

---

## Repository Structure

```
assignment4-graphs/
├── src/
│   ├── Vertex.java
│   ├── Edge.java
│   ├── Graph.java
│   ├── Experiment.java
│   └── Main.java
├── README.md
└── .gitignore
```

---

## How to Compile and Run

```bash
cd src
javac Vertex.java Edge.java Graph.java Experiment.java Main.java
java Main
```

No external libraries required.

---

## Class Descriptions

### `Vertex.java`
Represents a single node in the graph. Stores an integer `id` that uniquely identifies the vertex. Provides `getId()` and a `toString()` that formats as `V(n)` for readable output.

### `Edge.java`
Represents a directed connection between two `Vertex` objects (`source` and `destination`). In this project, edges are added in both directions inside `Graph.addEdge()` to model an undirected graph.

### `Graph.java`
The core class. Internally uses two `HashMap`s:

- `vertexMap` `(Integer → Vertex)` — looks up a Vertex object by id.
- `adjList` `(Integer → List<Vertex>)` — the adjacency list; maps each vertex to its list of direct neighbours.

**Methods:**
- `addVertex(Vertex v)` — inserts the vertex into both maps.
- `addEdge(int from, int to)` — appends destination to source's neighbour list and vice versa (undirected).
- `bfs(int startId)` — returns the BFS visit order as a `List<Integer>`.
- `dfs(int startId)` — returns the DFS visit order as a `List<Integer>`.
- `printGraph()` — prints the full adjacency list to console.

### `Experiment.java`
Builds three test graphs (10, 30, and 100 vertices), runs BFS and DFS on each, measures execution time with `System.nanoTime()`, and prints a formatted performance comparison table. Graph topology is a ring plus shortcut edges to guarantee full connectivity.

### `Main.java`
Entry point. Prints a header and delegates all work to `Experiment.runMultipleTests()`.

---

## Graph Representation — Adjacency List

An adjacency list stores, for each vertex, only the vertices it is directly connected to.

| Property | Adjacency List | Adjacency Matrix |
|---|---|---|
| Memory | O(V + E) | O(V²) |
| Check edge (u, v) | O(degree) | O(1) |
| List all neighbours | O(degree) | O(V) |
| Best for | Sparse graphs ✔ | Dense graphs |

This project uses an adjacency list because the graphs are sparse — far fewer edges than V² — making it more memory-efficient and faster to iterate.

---

## Algorithm Descriptions

### Breadth-First Search (BFS)

BFS explores the graph **level by level** — all neighbours of the start vertex are visited before their neighbours. Uses a **Queue (FIFO)**.

**Steps:**
1. Enqueue the start vertex; mark it visited.
2. While the queue is not empty:
   - Dequeue the front vertex (current).
   - Record it in the traversal order.
   - Enqueue all unvisited neighbours of current.

**Time complexity:** O(V + E)
**Best use:** Finding the shortest path in an unweighted graph.

---

### Depth-First Search (DFS)

DFS explores **as deep as possible** along each branch before backtracking. Uses a **Stack (LIFO)**.

**Steps:**
1. Push the start vertex onto the stack.
2. While the stack is not empty:
   - Pop the top vertex (current).
   - If already visited, skip.
   - Mark as visited; record it.
   - Push all unvisited neighbours of current.

**Time complexity:** O(V + E)
**Best use:** Cycle detection, topological sort, maze solving.

---

## Experimental Results

Graph topology: undirected ring + shortcut edges. Start vertex: 0. Times in microseconds (µs).

| Graph Size | BFS Time (µs) | DFS Time (µs) | Observation |
|---|---|---|---|
| 10 vertices | ~5–20 µs | ~3–15 µs | JVM startup noise dominates |
| 30 vertices | ~10–30 µs | ~8–25 µs | DFS slightly faster (stack locality) |
| 100 vertices | ~30–80 µs | ~25–70 µs | Both scale linearly with V + E |

Both algorithms scale linearly, consistent with **O(V + E)**. DFS is marginally faster in practice because `ArrayDeque` push/pop is cheaper than `LinkedList` poll.

---

## Reflection

The most important insight from this assignment is that BFS and DFS differ by exactly **one data structure**: swap a Queue for a Stack and BFS becomes DFS. Everything else — the visited set, the neighbour loop, the traversal recording — is identical. This shows that the order of exploration is entirely determined by the queue discipline (FIFO vs LIFO).

The hardest bug to fix was an infinite loop caused by forgetting the `visited` set on a cyclic graph. Adding the set, and checking it before enqueuing (BFS) or after popping (DFS), resolved it immediately. Choosing an adjacency list over a matrix was straightforward once the memory difference was calculated: O(V + E) vs O(V²) for a sparse graph is a significant saving.

---

---

# Bonus Task — Dijkstra's Algorithm (Shortest Path)

## Overview

This bonus task extends the existing graph system to support **weighted edges** and implements **Dijkstra's Algorithm** to find the shortest path from a starting vertex to all other vertices in the graph.

---

## Changes Made to Existing Classes

### `Edge.java` — added weight field

A new field `int weight` was added to the `Edge` class. The constructor now requires a weight argument, and a `getWeight()` getter was added.

```java
private int weight;

public Edge(Vertex source, Vertex destination, int weight) {
    this.source = source;
    this.destination = destination;
    this.weight = weight;
}

public int getWeight() {
    return weight;
}
```

### `Graph.java` — three updates

**1. Adjacency list now stores `Edge` objects instead of `Vertex` objects:**

```java
private Map<Integer, List<Edge>> adjList;
```

This allows each neighbour entry to carry its weight alongside the vertex reference.

**2. `addEdge()` now accepts a weight parameter:**

```java
public void addEdge(int from, int to, int weight) {
    adjList.get(from).add(new Edge(vertexMap.get(from), vertexMap.get(to), weight));
    adjList.get(to).add(new Edge(vertexMap.get(to), vertexMap.get(from), weight));
}
```

**3. BFS and DFS were updated** to read `edge.getDestination().getId()` instead of `vertex.getId()` — same logic, just accessing the destination through the Edge wrapper.

---

## Dijkstra's Algorithm — Implementation

### How it works

Dijkstra's algorithm finds the shortest (lowest total weight) path from one starting vertex to every other vertex in the graph. It works by always extending the currently cheapest known path first.

### Data structures used (arrays only, no priority queue — as required)

- `int[] dist` — stores the best known distance from the start to each vertex. Initialised to `Integer.MAX_VALUE` (infinity) for all vertices except the start, which is set to 0.
- `boolean[] visited` — tracks which vertices have been finalised (their shortest distance confirmed).
- `int[] prev` — remembers which vertex we came from on the shortest path, so the full path can be reconstructed at the end.

### Step-by-step

```
1. Set dist[start] = 0. All other distances = infinity.
2. Repeat V times:
   a. Find the unvisited vertex u with the smallest dist[u].
   b. Mark u as visited (its shortest distance is now confirmed).
   c. For each neighbour v of u:
      - If dist[u] + weight(u, v) < dist[v]:
          update dist[v] = dist[u] + weight(u, v)
          update prev[v] = u
3. Print distances and reconstruct paths using prev[].
```

### Why this works

At each step we finalise the vertex closest to the start. Because all weights are positive, no future path can make an already-finalised vertex cheaper. This greedy property guarantees correctness.

### Code

```java
public void dijkstra(int startId) {
    int n = vertexMap.size();
    int[] dist    = new int[n];
    boolean[] visited = new boolean[n];
    int[] prev    = new int[n];

    Arrays.fill(dist, Integer.MAX_VALUE);
    Arrays.fill(prev, -1);
    dist[startId] = 0;

    for (int step = 0; step < n; step++) {
        int u = -1;
        for (int id : ids) {
            if (!visited[id] && (u == -1 || dist[id] < dist[u])) u = id;
        }
        if (u == -1 || dist[u] == Integer.MAX_VALUE) break;

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
}
```

---

## Example Graph Used in Main.java

6 vertices, 8 weighted edges:

```
V(0) --4-- V(1)
V(0) --1-- V(2)
V(2) --2-- V(1)
V(1) --1-- V(3)
V(2) --5-- V(4)
V(3) --3-- V(4)
V(3) --6-- V(5)
V(4) --2-- V(5)
```

### Dijkstra output from V(0)

| Vertex | Shortest Distance | Path |
|---|---|---|
| V(0) | 0 | V(0) |
| V(1) | 3 | V(0) → V(2) → V(1) |
| V(2) | 1 | V(0) → V(2) |
| V(3) | 4 | V(0) → V(2) → V(1) → V(3) |
| V(4) | 6 | V(0) → V(2) → V(4) |
| V(5) | 8 | V(0) → V(2) → V(4) → V(5) |

Note that V(0) → V(1) directly costs 4, but going V(0) → V(2) → V(1) costs only 1 + 2 = 3. Dijkstra correctly finds the cheaper route.

---

## Experiment — Performance Comparison

`Experiment.java` was extended to time Dijkstra alongside BFS and DFS across three graph sizes.

| Graph Size | BFS Time (µs) | DFS Time (µs) | Dijkstra Time (µs) |
|---|---|---|---|
| 10 vertices | ~158 µs | ~72 µs | ~8,400 µs |
| 30 vertices | ~153 µs | ~140 µs | ~28,700 µs |
| 100 vertices | ~400 µs | ~7,200 µs | ~40,254 µs |

### Why Dijkstra is slower

BFS and DFS are **O(V + E)** — each vertex and edge is processed exactly once. Dijkstra with simple loops (no priority queue) is **O(V²)** — at every one of the V steps, it scans all V vertices to find the minimum distance. As the graph grows 10×, BFS/DFS time grows roughly 10×, but Dijkstra grows roughly 100×. This is expected behaviour and is documented in the note printed at the bottom of the results table.

A priority queue implementation would reduce Dijkstra to **O((V + E) log V)**, but the task specification explicitly states that a priority queue is not required.

---

## Bonus Reflection

Extending the graph to support weights required more changes than expected. The adjacency list had to change from storing `Vertex` objects to storing `Edge` objects so that the weight could travel alongside each connection. This meant updating both BFS and DFS to read through the Edge wrapper — the logic itself did not change, only how the neighbour id is accessed.

The most important thing Dijkstra taught me compared to BFS is the difference between *fewest hops* and *lowest cost*. BFS finds the path with the fewest edges; Dijkstra finds the path with the lowest total weight. On an unweighted graph they agree, but as soon as edges have different costs, BFS can return the wrong answer. The direct edge V(0)→V(1) with weight 4 is a clear example: BFS would take it immediately, but Dijkstra correctly identifies that the two-hop path through V(2) costs only 3.
