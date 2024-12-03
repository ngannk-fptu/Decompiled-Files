/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.scoring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.GraphIterables;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;

public final class EigenvectorCentrality<V, E>
implements VertexScoringAlgorithm<V, Double> {
    public static final int MAX_ITERATIONS_DEFAULT = 100;
    public static final double TOLERANCE_DEFAULT = 1.0E-4;
    private final Graph<V, E> g;
    private Map<V, Double> scores;

    public EigenvectorCentrality(Graph<V, E> g) {
        this(g, 100, 1.0E-4);
    }

    public EigenvectorCentrality(Graph<V, E> g, int maxIterations) {
        this(g, maxIterations, 1.0E-4);
    }

    public EigenvectorCentrality(Graph<V, E> g, int maxIterations, double tolerance) {
        this.g = g;
        this.scores = new HashMap<V, Double>();
        this.validate(maxIterations, tolerance);
        this.run(maxIterations, tolerance);
    }

    @Override
    public Map<V, Double> getScores() {
        return Collections.unmodifiableMap(this.scores);
    }

    @Override
    public Double getVertexScore(V v) {
        if (!this.g.containsVertex(v)) {
            throw new IllegalArgumentException("Cannot return score of unknown vertex");
        }
        return this.scores.get(v);
    }

    private void validate(int maxIterations, double tolerance) {
        if (maxIterations <= 0) {
            throw new IllegalArgumentException("Maximum iterations must be positive");
        }
        if (tolerance <= 0.0) {
            throw new IllegalArgumentException("Tolerance not valid, must be positive");
        }
    }

    private void run(int maxIterations, double tolerance) {
        int totalVertices = this.g.vertexSet().size();
        GraphIterables<V, E> iterables = this.g.iterables();
        double initScore = Math.sqrt(1.0 / (double)totalVertices);
        for (V v : iterables.vertices()) {
            this.scores.put((Double)v, initScore);
        }
        Map<Double, Double> nextScores = new HashMap<Double, Double>();
        double l2Norm = tolerance;
        while (maxIterations > 0 && l2Norm >= tolerance) {
            double sumOfSquares = 0.0;
            for (V v : iterables.vertices()) {
                double vNewValue = 0.0;
                for (Object e : iterables.incomingEdgesOf(v)) {
                    V w = Graphs.getOppositeVertex(this.g, e, v);
                    vNewValue += this.scores.get(w) * this.g.getEdgeWeight(e);
                }
                sumOfSquares += vNewValue * vNewValue;
                nextScores.put((Double)v, vNewValue);
            }
            double l2NormFactor = 1.0 / Math.sqrt(sumOfSquares);
            double sumOfDiffs2 = 0.0;
            for (Object v : iterables.vertices()) {
                double score = (Double)nextScores.get(v) * l2NormFactor;
                nextScores.put((Double)v, score);
                double d = this.scores.get(v) - score;
                sumOfDiffs2 += d * d;
            }
            Map<V, Double> tmp = this.scores;
            this.scores = nextScores;
            nextScores = tmp;
            l2Norm = Math.sqrt(sumOfDiffs2);
            --maxIterations;
        }
    }
}

