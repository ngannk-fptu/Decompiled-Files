/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.clustering;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.alg.scoring.EdgeBetweennessCentrality;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;

public class GirvanNewmanClustering<V, E>
implements ClusteringAlgorithm<V> {
    private Graph<V, E> graph;
    private int k;
    private final Iterable<V> startVertices;
    private final EdgeBetweennessCentrality.OverflowStrategy overflowStrategy;

    public GirvanNewmanClustering(Graph<V, E> graph, int k) {
        this(graph, k, EdgeBetweennessCentrality.OverflowStrategy.THROW_EXCEPTION_ON_OVERFLOW, graph.vertexSet());
    }

    public GirvanNewmanClustering(Graph<V, E> graph, int k, EdgeBetweennessCentrality.OverflowStrategy overflowStrategy, Iterable<V> startVertices) {
        this.graph = Objects.requireNonNull(graph);
        if (k < 1 || k > graph.vertexSet().size()) {
            throw new IllegalArgumentException("Illegal number of clusters");
        }
        this.k = k;
        this.overflowStrategy = overflowStrategy;
        this.startVertices = startVertices == null ? graph.vertexSet() : startVertices;
    }

    @Override
    public ClusteringAlgorithm.Clustering<V> getClustering() {
        Graph<V, DefaultEdge> graphCopy = GraphTypeBuilder.forGraphType(this.graph.getType()).edgeSupplier(SupplierUtil.DEFAULT_EDGE_SUPPLIER).vertexSupplier(this.graph.getVertexSupplier()).buildGraph();
        for (V v : this.graph.iterables().vertices()) {
            graphCopy.addVertex(v);
        }
        for (Object e : this.graph.iterables().edges()) {
            V sourceVertex = this.graph.getEdgeSource(e);
            V targetVertex = this.graph.getEdgeTarget(e);
            graphCopy.addEdge(sourceVertex, targetVertex);
        }
        List<Set<V>> ccs;
        while ((ccs = new ConnectivityInspector<V, DefaultEdge>(graphCopy).connectedSets()).size() != this.k) {
            EdgeBetweennessCentrality<V, DefaultEdge> bc = new EdgeBetweennessCentrality<V, DefaultEdge>(graphCopy, this.overflowStrategy, this.startVertices);
            DefaultEdge maxEdge = null;
            double maxCentrality = 0.0;
            for (Map.Entry<DefaultEdge, Double> entry : bc.getScores().entrySet()) {
                if (Double.compare(entry.getValue(), maxCentrality) <= 0 && maxEdge != null) continue;
                maxEdge = entry.getKey();
                maxCentrality = entry.getValue();
            }
            graphCopy.removeEdge(maxEdge);
        }
        return new ClusteringAlgorithm.ClusteringImpl<V>(ccs);
    }
}

