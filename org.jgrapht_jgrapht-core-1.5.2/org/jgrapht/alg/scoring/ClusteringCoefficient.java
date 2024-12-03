/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.scoring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphMetrics;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jgrapht.alg.util.NeighborCache;

public class ClusteringCoefficient<V, E>
implements VertexScoringAlgorithm<V, Double> {
    private final Graph<V, E> graph;
    private Map<V, Double> scores;
    private boolean fullyComputedMap = false;
    private boolean computed = false;
    private double globalClusteringCoefficient;
    private boolean computedAverage = false;
    private double averageClusteringCoefficient;

    public ClusteringCoefficient(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
        this.scores = new HashMap<V, Double>();
    }

    public double getGlobalClusteringCoefficient() {
        if (!this.computed) {
            this.computeGlobalClusteringCoefficient();
        }
        return this.globalClusteringCoefficient;
    }

    public double getAverageClusteringCoefficient() {
        if (this.graph.vertexSet().isEmpty()) {
            return 0.0;
        }
        if (!this.computedAverage) {
            this.computeFullScoreMap();
            this.computedAverage = true;
            this.averageClusteringCoefficient = 0.0;
            for (double value : this.scores.values()) {
                this.averageClusteringCoefficient += value;
            }
            this.averageClusteringCoefficient /= (double)this.graph.vertexSet().size();
        }
        return this.averageClusteringCoefficient;
    }

    private void computeGlobalClusteringCoefficient() {
        NeighborCache<V, E> neighborCache = new NeighborCache<V, E>(this.graph);
        this.computed = true;
        double numberTriplets = 0.0;
        for (V v : this.graph.vertexSet()) {
            if (this.graph.getType().isUndirected()) {
                numberTriplets += 1.0 * (double)this.graph.degreeOf(v) * (double)(this.graph.degreeOf(v) - 1) / 2.0;
                continue;
            }
            numberTriplets += 1.0 * (double)neighborCache.predecessorsOf(v).size() * (double)neighborCache.successorsOf(v).size();
        }
        this.globalClusteringCoefficient = (double)(3L * GraphMetrics.getNumberOfTriangles(this.graph)) / numberTriplets;
    }

    private double computeLocalClusteringCoefficient(V v) {
        if (this.scores.containsKey(v)) {
            return this.scores.get(v);
        }
        NeighborCache<V, E> neighborCache = new NeighborCache<V, E>(this.graph);
        Set<V> neighbourhood = neighborCache.neighborsOf(v);
        double k = neighbourhood.size();
        double numberTriplets = 0.0;
        for (V p : neighbourhood) {
            for (V q : neighbourhood) {
                if (!this.graph.containsEdge(p, q)) continue;
                numberTriplets += 1.0;
            }
        }
        if (k <= 1.0) {
            return 0.0;
        }
        return numberTriplets / (k * (k - 1.0));
    }

    private void computeFullScoreMap() {
        if (this.fullyComputedMap) {
            return;
        }
        this.fullyComputedMap = true;
        for (V v : this.graph.vertexSet()) {
            if (this.scores.containsKey(v)) continue;
            this.scores.put((Double)v, this.computeLocalClusteringCoefficient(v));
        }
    }

    @Override
    public Map<V, Double> getScores() {
        this.computeFullScoreMap();
        return Collections.unmodifiableMap(this.scores);
    }

    @Override
    public Double getVertexScore(V v) {
        if (!this.graph.containsVertex(v)) {
            throw new IllegalArgumentException("Cannot return score of unknown vertex");
        }
        return this.computeLocalClusteringCoefficient(v);
    }
}

