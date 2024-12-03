/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.scoring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.EdgeReversedGraph;

public class ClosenessCentrality<V, E>
implements VertexScoringAlgorithm<V, Double> {
    protected final Graph<V, E> graph;
    protected final boolean incoming;
    protected final boolean normalize;
    protected Map<V, Double> scores;

    public ClosenessCentrality(Graph<V, E> graph) {
        this(graph, false, true);
    }

    public ClosenessCentrality(Graph<V, E> graph, boolean incoming, boolean normalize) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.incoming = incoming;
        this.normalize = normalize;
        this.scores = null;
    }

    @Override
    public Map<V, Double> getScores() {
        if (this.scores == null) {
            this.compute();
        }
        return Collections.unmodifiableMap(this.scores);
    }

    @Override
    public Double getVertexScore(V v) {
        if (!this.graph.containsVertex(v)) {
            throw new IllegalArgumentException("Cannot return score of unknown vertex");
        }
        if (this.scores == null) {
            this.compute();
        }
        return this.scores.get(v);
    }

    protected ShortestPathAlgorithm<V, E> getShortestPathAlgorithm() {
        Graph<V, E> g = this.incoming && this.graph.getType().isDirected() ? new EdgeReversedGraph<V, E>(this.graph) : this.graph;
        boolean noNegativeWeights = true;
        for (E e : g.edgeSet()) {
            double w = g.getEdgeWeight(e);
            if (!(w < 0.0)) continue;
            noNegativeWeights = false;
            break;
        }
        BaseShortestPathAlgorithm alg = noNegativeWeights ? new DijkstraShortestPath<V, E>(g) : new FloydWarshallShortestPaths<V, E>(g);
        return alg;
    }

    protected void compute() {
        this.scores = new HashMap<V, Double>();
        ShortestPathAlgorithm<V, E> alg = this.getShortestPathAlgorithm();
        int n = this.graph.vertexSet().size();
        for (V v : this.graph.vertexSet()) {
            double sum = 0.0;
            ShortestPathAlgorithm.SingleSourcePaths<V, E> paths = alg.getPaths(v);
            for (V u : this.graph.vertexSet()) {
                if (u.equals(v)) continue;
                sum += paths.getWeight(u);
            }
            if (this.normalize) {
                this.scores.put((Double)v, (double)(n - 1) / sum);
                continue;
            }
            this.scores.put((Double)v, 1.0 / sum);
        }
    }
}

