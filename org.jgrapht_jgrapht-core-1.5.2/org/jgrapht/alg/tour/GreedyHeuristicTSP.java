/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.tour;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.HamiltonianCycleAlgorithmBase;
import org.jgrapht.alg.util.UnionFind;
import org.jgrapht.util.CollectionUtil;

public class GreedyHeuristicTSP<V, E>
extends HamiltonianCycleAlgorithmBase<V, E> {
    @Override
    public GraphPath<V, E> getTour(Graph<V, E> graph) {
        this.checkGraph(graph);
        int n = graph.vertexSet().size();
        if (n == 1) {
            return this.getSingletonTour(graph);
        }
        Deque edges = graph.edgeSet().stream().sorted((e1, e2) -> Double.compare(graph.getEdgeWeight(e1), graph.getEdgeWeight(e2))).collect(Collectors.toCollection(ArrayDeque::new));
        HashSet tourEdges = CollectionUtil.newHashSetWithExpectedSize(n);
        HashMap<V, Integer> vertexDegree = CollectionUtil.newHashMapWithExpectedSize(n);
        UnionFind<V> tourSet = new UnionFind<V>(graph.vertexSet());
        while (!edges.isEmpty() && tourEdges.size() < n) {
            V vertex2;
            Object edge = edges.pollFirst();
            V vertex1 = graph.getEdgeSource(edge);
            if (!this.canAddEdge(vertexDegree, tourSet, vertex1, vertex2 = graph.getEdgeTarget(edge), tourEdges.size() == n - 1)) continue;
            tourEdges.add(edge);
            vertexDegree.merge(vertex1, 1, Integer::sum);
            vertexDegree.merge(vertex2, 1, Integer::sum);
            tourSet.union(vertex1, vertex2);
        }
        return this.edgeSetToTour(tourEdges, graph);
    }

    private boolean canAddEdge(Map<V, Integer> vertexDegree, UnionFind<V> tourSet, V vertex1, V vertex2, boolean lastEdge) {
        if (vertexDegree.getOrDefault(vertex1, 0) > 1 || vertexDegree.getOrDefault(vertex2, 0) > 1) {
            return false;
        }
        return tourSet.inSameSet(vertex1, vertex2) ? lastEdge : !lastEdge;
    }
}

