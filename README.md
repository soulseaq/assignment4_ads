# Assignment 4 — Graph Traversal and Representation System

**Course:** Algorithms and Data Structures

**Algorithms:** Breadth-First Search (BFS) | Depth-First Search (DFS)

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
