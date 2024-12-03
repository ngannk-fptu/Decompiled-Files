/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.ToleranceDoubleComparator;
import org.jgrapht.graph.EdgeReversedGraph;

public class ALTAdmissibleHeuristic<V, E>
implements AStarAdmissibleHeuristic<V> {
    private final Graph<V, E> graph;
    private final Comparator<Double> comparator;
    private final Map<V, Map<V, Double>> fromLandmark;
    private final Map<V, Map<V, Double>> toLandmark;
    private final boolean directed;

    public ALTAdmissibleHeuristic(Graph<V, E> graph, Set<V> landmarks) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        Objects.requireNonNull(landmarks, "Landmarks cannot be null");
        if (landmarks.isEmpty()) {
            throw new IllegalArgumentException("At least one landmark must be provided");
        }
        this.fromLandmark = new HashMap<V, Map<V, Double>>();
        if (graph.getType().isDirected()) {
            this.directed = true;
            this.toLandmark = new HashMap<V, Map<V, Double>>();
        } else if (graph.getType().isUndirected()) {
            this.directed = false;
            this.toLandmark = this.fromLandmark;
        } else {
            throw new IllegalArgumentException("Graph must be directed or undirected");
        }
        this.comparator = new ToleranceDoubleComparator();
        for (V v : landmarks) {
            for (E e : graph.edgesOf(v)) {
                if (this.comparator.compare(graph.getEdgeWeight(e), 0.0) >= 0) continue;
                throw new IllegalArgumentException("Graph edge weights cannot be negative");
            }
            this.precomputeToFromLandmark(v);
        }
    }

    @Override
    public double getCostEstimate(V u, V t) {
        double maxEstimate = 0.0;
        if (u.equals(t)) {
            return maxEstimate;
        }
        if (this.fromLandmark.containsKey(u)) {
            return this.fromLandmark.get(u).get(t);
        }
        if (this.toLandmark.containsKey(t)) {
            return this.toLandmark.get(t).get(u);
        }
        for (V l : this.fromLandmark.keySet()) {
            double estimate;
            Map<V, Double> from = this.fromLandmark.get(l);
            if (this.directed) {
                Map<V, Double> to = this.toLandmark.get(l);
                estimate = Math.max(to.get(u) - to.get(t), from.get(t) - from.get(u));
            } else {
                estimate = Math.abs(from.get(u) - from.get(t));
            }
            if (!Double.isFinite(estimate)) continue;
            maxEstimate = Math.max(maxEstimate, estimate);
        }
        return maxEstimate;
    }

    private void precomputeToFromLandmark(V landmark) {
        ShortestPathAlgorithm.SingleSourcePaths<V, E> fromLandmarkPaths = new DijkstraShortestPath<V, E>(this.graph).getPaths(landmark);
        HashMap<V, Double> fromLandMarkDistances = new HashMap<V, Double>();
        for (V v : this.graph.vertexSet()) {
            fromLandMarkDistances.put(v, fromLandmarkPaths.getWeight(v));
        }
        this.fromLandmark.put(landmark, fromLandMarkDistances);
        if (this.directed) {
            EdgeReversedGraph<V, E> reverseGraph = new EdgeReversedGraph<V, E>(this.graph);
            ShortestPathAlgorithm.SingleSourcePaths<V, E> toLandmarkPaths = new DijkstraShortestPath<V, E>(reverseGraph).getPaths(landmark);
            HashMap<V, Double> toLandMarkDistances = new HashMap<V, Double>();
            for (V v : this.graph.vertexSet()) {
                toLandMarkDistances.put(v, toLandmarkPaths.getWeight(v));
            }
            this.toLandmark.put(landmark, toLandMarkDistances);
        }
    }

    @Override
    public <ET> boolean isConsistent(Graph<V, ET> graph) {
        return true;
    }
}

