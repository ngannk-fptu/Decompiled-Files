/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.densesubgraph;

import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.alg.densesubgraph.GoldbergMaximumDensitySubgraphAlgorithmBase;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;

public class GoldbergMaximumDensitySubgraphAlgorithm<V, E>
extends GoldbergMaximumDensitySubgraphAlgorithmBase<V, E> {
    public GoldbergMaximumDensitySubgraphAlgorithm(Graph<V, E> graph, V s, V t, double epsilon, Function<Graph<V, DefaultWeightedEdge>, MinimumSTCutAlgorithm<V, DefaultWeightedEdge>> algFactory) {
        super(graph, s, t, false, epsilon, algFactory);
    }

    public GoldbergMaximumDensitySubgraphAlgorithm(Graph<V, E> graph, V s, V t, double epsilon) {
        this(graph, s, t, epsilon, PushRelabelMFImpl::new);
    }

    @Override
    protected double getEdgeWeightFromSourceToVertex(V v) {
        return this.graph.edgeSet().size();
    }

    @Override
    protected double getEdgeWeightFromVertexToSink(V v) {
        return (double)this.graph.edgeSet().size() + 2.0 * this.guess - this.graph.outgoingEdgesOf(v).stream().mapToDouble(this.graph::getEdgeWeight).sum();
    }

    @Override
    protected double computeDensityNumerator(Graph<V, E> g) {
        return g.edgeSet().stream().mapToDouble(g::getEdgeWeight).sum();
    }

    @Override
    protected double computeDensityDenominator(Graph<V, E> g) {
        return g.vertexSet().size();
    }
}

