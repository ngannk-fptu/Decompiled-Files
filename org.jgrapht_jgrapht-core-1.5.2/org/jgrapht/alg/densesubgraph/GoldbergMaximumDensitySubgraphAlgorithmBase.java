/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.densesubgraph;

import java.util.HashSet;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MaximumDensitySubgraphAlgorithm;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

public abstract class GoldbergMaximumDensitySubgraphAlgorithmBase<V, E>
implements MaximumDensitySubgraphAlgorithm<V, E> {
    private double lower;
    private double upper;
    private double epsilon;
    protected double guess;
    protected final Graph<V, E> graph;
    private Graph<V, E> densestSubgraph;
    private Graph<V, DefaultWeightedEdge> currentNetwork;
    private Set<V> currentVertices;
    private V s;
    private V t;
    private MinimumSTCutAlgorithm<V, DefaultWeightedEdge> minSTCutAlg;
    private boolean checkWeights;

    public GoldbergMaximumDensitySubgraphAlgorithmBase(Graph<V, E> graph, V s, V t, boolean checkWeights, double epsilon, Function<Graph<V, DefaultWeightedEdge>, MinimumSTCutAlgorithm<V, DefaultWeightedEdge>> algFactory) {
        if (graph.containsVertex(s) || graph.containsVertex(t)) {
            throw new IllegalArgumentException("Source or sink vertex already in graph");
        }
        this.s = Objects.requireNonNull(s, "Source vertex is null");
        this.t = Objects.requireNonNull(t, "Sink vertex is null");
        this.graph = Objects.requireNonNull(graph, "Graph is null");
        this.epsilon = epsilon;
        this.guess = 0.0;
        this.lower = 0.0;
        this.upper = this.computeDensityNumerator(this.graph);
        this.checkWeights = checkWeights;
        this.currentNetwork = this.buildNetwork();
        this.currentVertices = new HashSet<V>();
        this.initializeNetwork();
        this.checkForEmptySolution();
        this.minSTCutAlg = algFactory.apply(this.currentNetwork);
    }

    private Graph<V, DefaultWeightedEdge> buildNetwork() {
        return GraphTypeBuilder.directed().allowingMultipleEdges(true).allowingSelfLoops(true).weighted(true).edgeSupplier(DefaultWeightedEdge::new).buildGraph();
    }

    private void updateNetwork() {
        double minCapacity;
        for (V v : this.graph.vertexSet()) {
            this.currentNetwork.setEdgeWeight(this.currentNetwork.getEdge(v, this.t), this.getEdgeWeightFromVertexToSink(v));
            this.currentNetwork.setEdgeWeight(this.currentNetwork.getEdge(this.s, v), this.getEdgeWeightFromSourceToVertex(v));
        }
        if (this.checkWeights && (minCapacity = this.getMinimalCapacity()) < 0.0) {
            for (V v : this.graph.vertexSet()) {
                DefaultWeightedEdge e = this.currentNetwork.getEdge(v, this.t);
                this.currentNetwork.setEdgeWeight(e, this.currentNetwork.getEdgeWeight(e) - minCapacity);
                e = this.currentNetwork.getEdge(this.s, v);
                this.currentNetwork.setEdgeWeight(e, this.currentNetwork.getEdgeWeight(e) - minCapacity);
            }
        }
    }

    private double getMinimalCapacity() {
        DoubleStream sinkWeights;
        DoubleStream sourceWeights = this.graph.vertexSet().stream().mapToDouble(v -> this.currentNetwork.getEdgeWeight(this.currentNetwork.getEdge(v, this.t)));
        OptionalDouble min = DoubleStream.concat(sourceWeights, sinkWeights = this.graph.vertexSet().stream().mapToDouble(v -> this.currentNetwork.getEdgeWeight(this.currentNetwork.getEdge(this.s, v)))).min();
        return min.isPresent() ? min.getAsDouble() : 0.0;
    }

    private void initializeNetwork() {
        this.currentNetwork.addVertex(this.s);
        this.currentNetwork.addVertex(this.t);
        for (V v : this.graph.vertexSet()) {
            this.currentNetwork.addVertex(v);
            this.currentNetwork.addEdge(this.s, v);
            this.currentNetwork.addEdge(v, this.t);
        }
        for (Object e : this.graph.edgeSet()) {
            DefaultWeightedEdge e1 = this.currentNetwork.addEdge(this.graph.getEdgeSource(e), this.graph.getEdgeTarget(e));
            DefaultWeightedEdge e2 = this.currentNetwork.addEdge(this.graph.getEdgeTarget(e), this.graph.getEdgeSource(e));
            double weight = this.graph.getEdgeWeight(e);
            this.currentNetwork.setEdgeWeight(e1, weight);
            this.currentNetwork.setEdgeWeight(e2, weight);
        }
    }

    @Override
    public Graph<V, E> calculateDensest() {
        if (this.densestSubgraph != null) {
            return this.densestSubgraph;
        }
        while (Double.compare(this.upper - this.lower, this.epsilon) >= 0) {
            this.guess = this.lower + (this.upper - this.lower) / 2.0;
            this.updateNetwork();
            this.minSTCutAlg.calculateMinCut(this.s, this.t);
            Set<V> sourcePartition = this.minSTCutAlg.getSourcePartition();
            sourcePartition.remove(this.s);
            if (sourcePartition.isEmpty()) {
                this.upper = this.guess;
                continue;
            }
            this.lower = this.guess;
            this.currentVertices = new HashSet<V>(sourcePartition);
        }
        this.densestSubgraph = new AsSubgraph<V, E>(this.graph, this.currentVertices);
        return this.densestSubgraph;
    }

    @Override
    public double getDensity() {
        double denominator;
        if (this.densestSubgraph == null) {
            this.calculateDensest();
        }
        if ((denominator = this.computeDensityDenominator(this.densestSubgraph)) != 0.0) {
            return this.computeDensityNumerator(this.densestSubgraph) / denominator;
        }
        return 0.0;
    }

    protected abstract double getEdgeWeightFromSourceToVertex(V var1);

    protected abstract double getEdgeWeightFromVertexToSink(V var1);

    protected abstract double computeDensityNumerator(Graph<V, E> var1);

    protected abstract double computeDensityDenominator(Graph<V, E> var1);

    private void checkForEmptySolution() {
        if (Double.compare(this.computeDensityDenominator(this.graph), 0.0) == 0) {
            this.densestSubgraph = new AsSubgraph<V, E>(this.graph, null);
        }
    }
}

