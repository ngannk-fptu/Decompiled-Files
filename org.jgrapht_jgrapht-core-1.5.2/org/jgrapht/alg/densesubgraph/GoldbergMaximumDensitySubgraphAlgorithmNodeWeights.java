/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.densesubgraph;

import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.alg.densesubgraph.GoldbergMaximumDensitySubgraphAlgorithmBase;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;

public class GoldbergMaximumDensitySubgraphAlgorithmNodeWeights<V extends Pair<?, Double>, E>
extends GoldbergMaximumDensitySubgraphAlgorithmBase<V, E> {
    public GoldbergMaximumDensitySubgraphAlgorithmNodeWeights(Graph<V, E> graph, V s, V t, double epsilon, Function<Graph<V, DefaultWeightedEdge>, MinimumSTCutAlgorithm<V, DefaultWeightedEdge>> algFactory) {
        super(graph, s, t, true, epsilon, algFactory);
    }

    public GoldbergMaximumDensitySubgraphAlgorithmNodeWeights(Graph<V, E> graph, V s, V t, double epsilon) {
        this(graph, s, t, epsilon, PushRelabelMFImpl::new);
    }

    @Override
    protected double computeDensityNumerator(Graph<V, E> g) {
        double sum = g.edgeSet().stream().mapToDouble(g::getEdgeWeight).sum();
        for (Pair v : g.vertexSet()) {
            sum += ((Double)v.getSecond()).doubleValue();
        }
        return sum;
    }

    @Override
    protected double computeDensityDenominator(Graph<V, E> g) {
        return g.vertexSet().size();
    }

    @Override
    protected double getEdgeWeightFromSourceToVertex(V v) {
        return 0.0;
    }

    @Override
    protected double getEdgeWeightFromVertexToSink(V v) {
        return 2.0 * this.guess - this.graph.outgoingEdgesOf(v).stream().mapToDouble(this.graph::getEdgeWeight).sum() - 2.0 * (Double)((Pair)v).getSecond();
    }
}

