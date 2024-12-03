/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.matching.blossom.v5;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.blossom.v5.BlossomVOptions;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedPerfectMatching;
import org.jgrapht.alg.matching.blossom.v5.ObjectiveSense;
import org.jgrapht.graph.AsGraphUnion;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;

public class KolmogorovWeightedMatching<V, E>
implements MatchingAlgorithm<V, E> {
    private final Graph<V, E> initialGraph;
    private Graph<V, E> graph;
    private MatchingAlgorithm.Matching<V, E> matching;
    private KolmogorovWeightedPerfectMatching<V, E> perfectMatching;
    private BlossomVOptions options;
    private ObjectiveSense objectiveSense;

    public KolmogorovWeightedMatching(Graph<V, E> initialGraph) {
        this(initialGraph, KolmogorovWeightedPerfectMatching.DEFAULT_OPTIONS, ObjectiveSense.MAXIMIZE);
    }

    public KolmogorovWeightedMatching(Graph<V, E> initialGraph, ObjectiveSense objectiveSense) {
        this(initialGraph, KolmogorovWeightedPerfectMatching.DEFAULT_OPTIONS, objectiveSense);
    }

    public KolmogorovWeightedMatching(Graph<V, E> initialGraph, BlossomVOptions options) {
        this(initialGraph, options, ObjectiveSense.MAXIMIZE);
    }

    public KolmogorovWeightedMatching(Graph<V, E> initialGraph, BlossomVOptions options, ObjectiveSense objectiveSense) {
        this.initialGraph = Objects.requireNonNull(initialGraph);
        this.options = Objects.requireNonNull(options);
        this.objectiveSense = objectiveSense;
    }

    @Override
    public MatchingAlgorithm.Matching<V, E> getMatching() {
        if (this.matching == null) {
            this.lazyComputeMaximumWeightMatching();
        }
        return this.matching;
    }

    private void lazyComputeMaximumWeightMatching() {
        HashMap<V, V> duplicatedVertices = new HashMap<V, V>();
        GraphType type = this.initialGraph.getType();
        Graph graphCopy = GraphTypeBuilder.undirected().allowingMultipleEdges(type.isAllowingMultipleEdges()).allowingSelfLoops(type.isAllowingSelfLoops()).vertexSupplier(this.initialGraph.getVertexSupplier()).edgeSupplier(this.initialGraph.getEdgeSupplier()).weighted(type.isWeighted()).buildGraph();
        for (V v : this.initialGraph.vertexSet()) {
            duplicatedVertices.put(v, graphCopy.addVertex());
        }
        for (Iterator edge : this.initialGraph.edgeSet()) {
            Graphs.addEdgeWithVertices(graphCopy, duplicatedVertices.get(this.initialGraph.getEdgeSource(edge)), duplicatedVertices.get(this.initialGraph.getEdgeTarget(edge)), this.initialGraph.getEdgeWeight(edge));
        }
        HashMap<E, Double> zeroWeightFunction = new HashMap<E, Double>();
        for (Map.Entry entry : duplicatedVertices.entrySet()) {
            graphCopy.addVertex(entry.getKey());
            zeroWeightFunction.put(graphCopy.addEdge(entry.getKey(), entry.getValue()), 0.0);
        }
        this.graph = new AsGraphUnion<V, E>(new AsWeightedGraph<V, E>(graphCopy, zeroWeightFunction), this.initialGraph);
        this.perfectMatching = new KolmogorovWeightedPerfectMatching<V, E>(this.graph, this.options, this.objectiveSense);
        this.matching = this.perfectMatching.getMatching();
        Set<Object> matchingEdges = this.matching.getEdges();
        matchingEdges.removeIf(e -> !this.initialGraph.containsEdge(e));
        this.matching = new MatchingAlgorithm.MatchingImpl<V, E>(this.initialGraph, matchingEdges, this.matching.getWeight() / 2.0);
    }

    public boolean testOptimality() {
        return this.perfectMatching.getError() < 1.0E-9;
    }

    public double getError() {
        this.lazyComputeMaximumWeightMatching();
        return this.perfectMatching.getError();
    }
}

