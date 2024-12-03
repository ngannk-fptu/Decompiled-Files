/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphMetrics;
import org.jgrapht.GraphType;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.cycle.BergeGraphInspector;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.alg.cycle.WeakChordalityInspector;
import org.jgrapht.alg.interfaces.PartitioningAlgorithm;
import org.jgrapht.alg.partition.BipartitePartitioning;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;

public abstract class GraphTests {
    private static final String GRAPH_CANNOT_BE_NULL = "Graph cannot be null";
    private static final String GRAPH_MUST_BE_DIRECTED_OR_UNDIRECTED = "Graph must be directed or undirected";
    private static final String GRAPH_MUST_BE_UNDIRECTED = "Graph must be undirected";
    private static final String GRAPH_MUST_BE_DIRECTED = "Graph must be directed";
    private static final String GRAPH_MUST_BE_WEIGHTED = "Graph must be weighted";

    public static <V, E> boolean isEmpty(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        return graph.edgeSet().isEmpty();
    }

    public static <V, E> boolean isSimple(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        GraphType type = graph.getType();
        if (type.isSimple()) {
            return true;
        }
        for (V v : graph.vertexSet()) {
            HashSet<V> neighbors = new HashSet<V>();
            for (E e : graph.outgoingEdgesOf(v)) {
                V u = Graphs.getOppositeVertex(graph, e, v);
                if (!u.equals(v) && neighbors.add(u)) continue;
                return false;
            }
        }
        return true;
    }

    public static <V, E> boolean hasSelfLoops(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        if (!graph.getType().isAllowingSelfLoops()) {
            return false;
        }
        for (E e : graph.edgeSet()) {
            if (!graph.getEdgeSource(e).equals(graph.getEdgeTarget(e))) continue;
            return true;
        }
        return false;
    }

    public static <V, E> boolean hasMultipleEdges(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        if (!graph.getType().isAllowingMultipleEdges()) {
            return false;
        }
        for (V v : graph.vertexSet()) {
            HashSet<V> neighbors = new HashSet<V>();
            for (E e : graph.outgoingEdgesOf(v)) {
                V u = Graphs.getOppositeVertex(graph, e, v);
                if (neighbors.add(u)) continue;
                return true;
            }
        }
        return false;
    }

    public static <V, E> boolean isComplete(Graph<V, E> graph) {
        int allEdges;
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        int n = graph.vertexSet().size();
        if (graph.getType().isDirected()) {
            allEdges = Math.multiplyExact(n, n - 1);
        } else if (graph.getType().isUndirected()) {
            allEdges = n % 2 == 0 ? Math.multiplyExact(n / 2, n - 1) : Math.multiplyExact(n, (n - 1) / 2);
        } else {
            throw new IllegalArgumentException(GRAPH_MUST_BE_DIRECTED_OR_UNDIRECTED);
        }
        return graph.edgeSet().size() == allEdges && GraphTests.isSimple(graph);
    }

    public static <V, E> boolean isConnected(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        return new ConnectivityInspector<V, E>(graph).isConnected();
    }

    public static <V, E> boolean isBiconnected(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        return new BiconnectivityInspector<V, E>(graph).isBiconnected();
    }

    public static <V, E> boolean isWeaklyConnected(Graph<V, E> graph) {
        return GraphTests.isConnected(graph);
    }

    public static <V, E> boolean isStronglyConnected(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        if (graph.getType().isUndirected()) {
            return GraphTests.isConnected(graph);
        }
        return new KosarajuStrongConnectivityInspector<V, E>(graph).isStronglyConnected();
    }

    public static <V, E> boolean isTree(Graph<V, E> graph) {
        if (!graph.getType().isUndirected()) {
            throw new IllegalArgumentException(GRAPH_MUST_BE_UNDIRECTED);
        }
        return graph.edgeSet().size() == graph.vertexSet().size() - 1 && GraphTests.isConnected(graph);
    }

    public static <V, E> boolean isForest(Graph<V, E> graph) {
        if (!graph.getType().isUndirected()) {
            throw new IllegalArgumentException(GRAPH_MUST_BE_UNDIRECTED);
        }
        if (graph.vertexSet().isEmpty()) {
            return false;
        }
        int nrConnectedComponents = new ConnectivityInspector<V, E>(graph).connectedSets().size();
        return graph.edgeSet().size() + nrConnectedComponents == graph.vertexSet().size();
    }

    public static <V, E> boolean isOverfull(Graph<V, E> graph) {
        int maxDegree = graph.vertexSet().stream().mapToInt(graph::degreeOf).max().getAsInt();
        return (double)graph.edgeSet().size() > (double)maxDegree * Math.floor((double)graph.vertexSet().size() / 2.0);
    }

    public static <V, E> boolean isSplit(Graph<V, E> graph) {
        int m;
        GraphTests.requireUndirected(graph);
        if (!GraphTests.isSimple(graph) || graph.vertexSet().isEmpty()) {
            return false;
        }
        ArrayList degrees = new ArrayList(graph.vertexSet().size());
        degrees.addAll(graph.vertexSet().stream().map(graph::degreeOf).collect(Collectors.toList()));
        Collections.sort(degrees, Collections.reverseOrder());
        for (m = 1; m < degrees.size() && (Integer)degrees.get(m) >= m; ++m) {
        }
        --m;
        int left = 0;
        for (int i = 0; i <= m; ++i) {
            left += ((Integer)degrees.get(i)).intValue();
        }
        int right = m * (m + 1);
        for (int i = m + 1; i < degrees.size(); ++i) {
            right += ((Integer)degrees.get(i)).intValue();
        }
        return left == right;
    }

    public static <V, E> boolean isBipartite(Graph<V, E> graph) {
        return new BipartitePartitioning<V, E>(graph).isBipartite();
    }

    public static <V, E> boolean isBipartitePartition(Graph<V, E> graph, Set<? extends V> firstPartition, Set<? extends V> secondPartition) {
        return new BipartitePartitioning(graph).isValidPartitioning(new PartitioningAlgorithm.PartitioningImpl(Arrays.asList(firstPartition, secondPartition)));
    }

    public static <V, E> boolean isCubic(Graph<V, E> graph) {
        for (V v : graph.vertexSet()) {
            if (graph.degreeOf(v) == 3) continue;
            return false;
        }
        return true;
    }

    public static <V, E> boolean isEulerian(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        return new HierholzerEulerianCycle<V, E>().isEulerian(graph);
    }

    public static <V, E> boolean isChordal(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        return new ChordalityInspector<V, E>(graph).isChordal();
    }

    public static <V, E> boolean isWeaklyChordal(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        return new WeakChordalityInspector<V, E>(graph).isWeaklyChordal();
    }

    public static <V, E> boolean hasOreProperty(Graph<V, E> graph) {
        GraphTests.requireUndirected(graph);
        int n = graph.vertexSet().size();
        if (!graph.getType().isSimple() || n < 3) {
            return false;
        }
        ArrayList<V> vertexList = new ArrayList<V>(graph.vertexSet());
        for (int i = 0; i < vertexList.size(); ++i) {
            for (int j = i + 1; j < vertexList.size(); ++j) {
                Object w;
                Object v = vertexList.get(i);
                if (v.equals(w = vertexList.get(j)) || graph.containsEdge(v, w) || graph.degreeOf(v) + graph.degreeOf(w) >= n) continue;
                return false;
            }
        }
        return true;
    }

    public static <V, E> boolean isTriangleFree(Graph<V, E> graph) {
        return GraphMetrics.getNumberOfTriangles(graph) == 0L;
    }

    public static <V, E> boolean isPerfect(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        return new BergeGraphInspector<V, E>().isBerge(graph);
    }

    public static <V, E> boolean isPlanar(Graph<V, E> graph) {
        Objects.requireNonNull(graph, GRAPH_CANNOT_BE_NULL);
        return new BoyerMyrvoldPlanarityInspector<V, E>(graph).isPlanar();
    }

    public static <V, E> boolean isKuratowskiSubdivision(Graph<V, E> graph) {
        return GraphTests.isK33Subdivision(graph) || GraphTests.isK5Subdivision(graph);
    }

    public static <V, E> boolean isK33Subdivision(Graph<V, E> graph) {
        ArrayList<V> degree3 = new ArrayList<V>();
        for (V vertex : graph.vertexSet()) {
            int degree = graph.degreeOf(vertex);
            if (degree == 3) {
                degree3.add(vertex);
                continue;
            }
            if (degree == 2) continue;
            return false;
        }
        if (degree3.size() != 6) {
            return false;
        }
        Object vertex = degree3.remove(degree3.size() - 1);
        Set<V> reachable = GraphTests.reachableWithDegree(graph, vertex, 3);
        if (reachable.size() != 3) {
            return false;
        }
        degree3.removeAll(reachable);
        return reachable.equals(GraphTests.reachableWithDegree(graph, degree3.get(0), 3)) && reachable.equals(GraphTests.reachableWithDegree(graph, degree3.get(1), 3));
    }

    public static <V, E> boolean isK5Subdivision(Graph<V, E> graph) {
        HashSet<V> degree5 = new HashSet<V>();
        for (Object vertex : graph.vertexSet()) {
            int degree = graph.degreeOf(vertex);
            if (degree == 4) {
                degree5.add(vertex);
                continue;
            }
            if (degree == 2) continue;
            return false;
        }
        if (degree5.size() != 5) {
            return false;
        }
        for (Object vertex : degree5) {
            Set<V> reachable = GraphTests.reachableWithDegree(graph, vertex, 4);
            if (reachable.size() == 4 && degree5.containsAll(reachable) && !reachable.contains(vertex)) continue;
            return false;
        }
        return true;
    }

    private static <V, E> Set<V> reachableWithDegree(Graph<V, E> graph, V startVertex, int degree) {
        HashSet visited = new HashSet();
        HashSet<V> reachable = new HashSet<V>();
        ArrayDeque<V> queue = new ArrayDeque<V>();
        queue.add(startVertex);
        while (!queue.isEmpty()) {
            Object current = queue.poll();
            visited.add(current);
            for (E e : graph.edgesOf(current)) {
                V opposite = Graphs.getOppositeVertex(graph, e, current);
                if (visited.contains(opposite)) continue;
                if (graph.degreeOf(opposite) == degree) {
                    reachable.add(opposite);
                    continue;
                }
                queue.add(opposite);
            }
        }
        return reachable;
    }

    public static <V, E> Graph<V, E> requireDirected(Graph<V, E> graph, String message) {
        if (graph == null) {
            throw new NullPointerException(GRAPH_CANNOT_BE_NULL);
        }
        if (!graph.getType().isDirected()) {
            throw new IllegalArgumentException(message);
        }
        return graph;
    }

    public static <V, E> Graph<V, E> requireDirected(Graph<V, E> graph) {
        return GraphTests.requireDirected(graph, GRAPH_MUST_BE_DIRECTED);
    }

    public static <V, E> Graph<V, E> requireUndirected(Graph<V, E> graph, String message) {
        if (graph == null) {
            throw new NullPointerException(GRAPH_CANNOT_BE_NULL);
        }
        if (!graph.getType().isUndirected()) {
            throw new IllegalArgumentException(message);
        }
        return graph;
    }

    public static <V, E> Graph<V, E> requireUndirected(Graph<V, E> graph) {
        return GraphTests.requireUndirected(graph, GRAPH_MUST_BE_UNDIRECTED);
    }

    public static <V, E> Graph<V, E> requireDirectedOrUndirected(Graph<V, E> graph, String message) {
        if (graph == null) {
            throw new NullPointerException(GRAPH_CANNOT_BE_NULL);
        }
        if (!graph.getType().isDirected() && !graph.getType().isUndirected()) {
            throw new IllegalArgumentException(message);
        }
        return graph;
    }

    public static <V, E> Graph<V, E> requireDirectedOrUndirected(Graph<V, E> graph) {
        return GraphTests.requireDirectedOrUndirected(graph, GRAPH_MUST_BE_DIRECTED_OR_UNDIRECTED);
    }

    public static <V, E> Graph<V, E> requireWeighted(Graph<V, E> graph) {
        if (graph == null) {
            throw new NullPointerException(GRAPH_CANNOT_BE_NULL);
        }
        if (!graph.getType().isWeighted()) {
            throw new IllegalArgumentException(GRAPH_MUST_BE_WEIGHTED);
        }
        return graph;
    }
}

