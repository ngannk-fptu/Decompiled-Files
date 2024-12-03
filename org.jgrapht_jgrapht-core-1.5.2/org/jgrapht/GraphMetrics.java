/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.GraphMeasurer;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.util.CollectionUtil;

public abstract class GraphMetrics {
    public static <V, E> double getDiameter(Graph<V, E> graph) {
        return new GraphMeasurer<V, E>(graph).getDiameter();
    }

    public static <V, E> double getRadius(Graph<V, E> graph) {
        return new GraphMeasurer<V, E>(graph).getRadius();
    }

    public static <V, E> int getGirth(Graph<V, E> graph) {
        int nil = -1;
        boolean isAllowingMultipleEdges = graph.getType().isAllowingMultipleEdges();
        ArrayList<V> vertices = new ArrayList<V>(graph.vertexSet());
        HashMap indexMap = new HashMap();
        for (int i = 0; i < vertices.size(); ++i) {
            indexMap.put(vertices.get(i), i);
        }
        int girth = Integer.MAX_VALUE;
        int[] depth = new int[vertices.size()];
        ArrayDeque<Object> queue = new ArrayDeque<Object>();
        if (graph.getType().isAllowingSelfLoops()) {
            for (Object v : vertices) {
                if (!graph.containsEdge(v, v)) continue;
                return 1;
            }
        }
        NeighborCache neighborIndex = new NeighborCache(graph);
        if (graph.getType().isUndirected()) {
            int[] parent = new int[vertices.size()];
            for (int i = 0; i < vertices.size() - 2 && girth > 3; ++i) {
                int depthU;
                Arrays.fill(depth, -1);
                Arrays.fill(parent, -1);
                queue.clear();
                depth[i] = 0;
                queue.add(vertices.get(i));
                do {
                    Object u = queue.poll();
                    int indexU = (Integer)indexMap.get(u);
                    depthU = depth[indexU];
                    for (V v : neighborIndex.neighborsOf(u)) {
                        int indexV = (Integer)indexMap.get(v);
                        if (parent[indexU] == indexV && (!isAllowingMultipleEdges || graph.getAllEdges(u, v).size() == 1)) continue;
                        int depthV = depth[indexV];
                        if (depthV == -1) {
                            queue.add(v);
                            depth[indexV] = depthU + 1;
                            parent[indexV] = indexU;
                            continue;
                        }
                        girth = Math.min(girth, depthU + depthV + 1);
                    }
                } while (!queue.isEmpty() && 2 * (depthU + 1) - 1 < girth);
            }
        } else {
            for (int i = 0; i < vertices.size() - 1 && girth > 2; ++i) {
                int depthU;
                Arrays.fill(depth, -1);
                queue.clear();
                depth[i] = 0;
                queue.add(vertices.get(i));
                do {
                    Object u = queue.poll();
                    int indexU = (Integer)indexMap.get(u);
                    depthU = depth[indexU];
                    for (V v : neighborIndex.successorsOf(u)) {
                        int indexV = (Integer)indexMap.get(v);
                        int depthV = depth[indexV];
                        if (depthV == -1) {
                            queue.add(v);
                            depth[indexV] = depthU + 1;
                            continue;
                        }
                        if (depthV != 0) continue;
                        girth = Math.min(girth, depthU + depthV + 1);
                    }
                } while (!queue.isEmpty() && depthU + 1 < girth);
            }
        }
        assert (graph.getType().isUndirected() && graph.getType().isSimple() && girth >= 3 || graph.getType().isAllowingSelfLoops() && girth >= 1 || girth >= 2 && (graph.getType().isDirected() || graph.getType().isAllowingMultipleEdges()));
        return girth;
    }

    static <V, E> long naiveCountTriangles(Graph<V, E> graph, List<V> vertexSubset) {
        long total = 0L;
        if (graph.getType().isAllowingMultipleEdges()) {
            for (int i = 0; i < vertexSubset.size(); ++i) {
                for (int j = i + 1; j < vertexSubset.size(); ++j) {
                    for (int k = j + 1; k < vertexSubset.size(); ++k) {
                        int wuEdgeCount;
                        int vwEdgeCount;
                        V u = vertexSubset.get(i);
                        V v = vertexSubset.get(j);
                        V w = vertexSubset.get(k);
                        int uvEdgeCount = graph.getAllEdges(u, v).size();
                        if (uvEdgeCount == 0 || (vwEdgeCount = graph.getAllEdges(v, w).size()) == 0 || (wuEdgeCount = graph.getAllEdges(w, u).size()) == 0) continue;
                        total += (long)(uvEdgeCount * vwEdgeCount * wuEdgeCount);
                    }
                }
            }
        } else {
            for (int i = 0; i < vertexSubset.size(); ++i) {
                for (int j = i + 1; j < vertexSubset.size(); ++j) {
                    for (int k = j + 1; k < vertexSubset.size(); ++k) {
                        V u = vertexSubset.get(i);
                        V v = vertexSubset.get(j);
                        V w = vertexSubset.get(k);
                        if (!graph.containsEdge(u, v) || !graph.containsEdge(v, w) || !graph.containsEdge(w, u)) continue;
                        ++total;
                    }
                }
            }
        }
        return total;
    }

    public static <V, E> long getNumberOfTriangles(Graph<V, E> graph) {
        GraphTests.requireUndirected(graph);
        int sqrtV = (int)Math.sqrt(graph.vertexSet().size());
        ArrayList<V> vertexList = new ArrayList<V>(graph.vertexSet());
        HashMap<V, Integer> vertexOrder = CollectionUtil.newHashMapWithExpectedSize(graph.vertexSet().size());
        int k = 0;
        for (V v : graph.vertexSet()) {
            vertexOrder.put(v, k++);
        }
        Comparator<Object> comparator = Comparator.comparingInt(graph::degreeOf).thenComparingInt(System::identityHashCode).thenComparingInt(vertexOrder::get);
        vertexList.sort(comparator);
        List heavyHitterVertices = vertexList.stream().filter(x -> graph.degreeOf(x) >= sqrtV).collect(Collectors.toCollection(ArrayList::new));
        long numberTriangles = GraphMetrics.naiveCountTriangles(graph, heavyHitterVertices);
        for (E edge : graph.edgeSet()) {
            V v2;
            V v1 = graph.getEdgeSource(edge);
            if (v1 == (v2 = graph.getEdgeTarget(edge)) || graph.degreeOf(v1) >= sqrtV && graph.degreeOf(v2) >= sqrtV) continue;
            if (comparator.compare(v1, v2) > 0) {
                V tmp = v1;
                v1 = v2;
                v2 = tmp;
            }
            for (E e : graph.edgesOf(v1)) {
                V u = Graphs.getOppositeVertex(graph, e, v1);
                if (u == v1 || u == v2 || comparator.compare(v2, u) > 0 || !graph.containsEdge(u, v2)) continue;
                ++numberTriangles;
            }
        }
        return numberTriangles;
    }
}

