/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.scoring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;

public final class KatzCentrality<V, E>
implements VertexScoringAlgorithm<V, Double> {
    public static final int MAX_ITERATIONS_DEFAULT = 100;
    public static final double TOLERANCE_DEFAULT = 1.0E-4;
    public static final double DAMPING_FACTOR_DEFAULT = 0.01;
    private final Graph<V, E> g;
    private Map<V, Double> scores;

    public static final <V> ToDoubleFunction<V> exogenousFactorDefaultFunction() {
        return x -> 1.0;
    }

    public KatzCentrality(Graph<V, E> g) {
        this(g, 0.01, KatzCentrality.exogenousFactorDefaultFunction(), 100, 1.0E-4);
    }

    public KatzCentrality(Graph<V, E> g, double dampingFactor) {
        this(g, dampingFactor, KatzCentrality.exogenousFactorDefaultFunction(), 100, 1.0E-4);
    }

    public KatzCentrality(Graph<V, E> g, double dampingFactor, int maxIterations) {
        this(g, dampingFactor, KatzCentrality.exogenousFactorDefaultFunction(), maxIterations, 1.0E-4);
    }

    public KatzCentrality(Graph<V, E> g, double dampingFactor, int maxIterations, double tolerance) {
        this(g, dampingFactor, KatzCentrality.exogenousFactorDefaultFunction(), maxIterations, tolerance);
    }

    public KatzCentrality(Graph<V, E> g, double dampingFactor, ToDoubleFunction<V> exogenousFactorFunction) {
        this(g, dampingFactor, exogenousFactorFunction, 100, 1.0E-4);
    }

    public KatzCentrality(Graph<V, E> g, double dampingFactor, ToDoubleFunction<V> exogenousFactorFunction, int maxIterations) {
        this(g, dampingFactor, exogenousFactorFunction, maxIterations, 1.0E-4);
    }

    public KatzCentrality(Graph<V, E> g, double dampingFactor, ToDoubleFunction<V> exogenousFactorFunction, int maxIterations, double tolerance) {
        this.g = g;
        this.scores = new HashMap<V, Double>();
        this.validate(dampingFactor, maxIterations, tolerance);
        this.run(dampingFactor, exogenousFactorFunction, maxIterations, tolerance);
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

    private void validate(double dampingFactor, int maxIterations, double tolerance) {
        if (maxIterations <= 0) {
            throw new IllegalArgumentException("Maximum iterations must be positive");
        }
        if (dampingFactor < 0.0) {
            throw new IllegalArgumentException("Damping factor not valid");
        }
        if (tolerance <= 0.0) {
            throw new IllegalArgumentException("Tolerance not valid, must be positive");
        }
    }

    private void run(double dampingFactor, ToDoubleFunction<V> exofactorFunction, int maxIterations, double tolerance) {
        for (V v : this.g.vertexSet()) {
            this.scores.put((Double)v, exofactorFunction.applyAsDouble(v));
        }
        Map<Double, Double> nextScores = new HashMap<V, Double>();
        double maxChange = tolerance;
        while (maxIterations > 0 && maxChange >= tolerance) {
            maxChange = 0.0;
            for (V v : this.g.vertexSet()) {
                double contribution = 0.0;
                for (E e : this.g.incomingEdgesOf(v)) {
                    V w = Graphs.getOppositeVertex(this.g, e, v);
                    contribution += dampingFactor * this.scores.get(w) * this.g.getEdgeWeight(e);
                }
                double vOldValue = this.scores.get(v);
                double vNewValue = contribution + exofactorFunction.applyAsDouble(v);
                maxChange = Math.max(maxChange, Math.abs(vNewValue - vOldValue));
                nextScores.put((Double)v, vNewValue);
            }
            Map<V, Double> tmp = this.scores;
            this.scores = nextScores;
            nextScores = tmp;
            --maxIterations;
        }
    }
}

