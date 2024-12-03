/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.HashMap;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.ListSingleSourcePathsImpl;
import org.jgrapht.graph.GraphWalk;

abstract class BaseShortestPathAlgorithm<V, E>
implements ShortestPathAlgorithm<V, E> {
    protected static final String GRAPH_CONTAINS_A_NEGATIVE_WEIGHT_CYCLE = "Graph contains a negative-weight cycle";
    protected static final String GRAPH_MUST_CONTAIN_THE_SOURCE_VERTEX = "Graph must contain the source vertex!";
    protected static final String GRAPH_MUST_CONTAIN_THE_SINK_VERTEX = "Graph must contain the sink vertex!";
    protected final Graph<V, E> graph;

    public BaseShortestPathAlgorithm(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph is null");
    }

    @Override
    public ShortestPathAlgorithm.SingleSourcePaths<V, E> getPaths(V source) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("graph must contain the source vertex");
        }
        HashMap paths = new HashMap();
        for (V v : this.graph.vertexSet()) {
            paths.put(v, this.getPath(source, v));
        }
        return new ListSingleSourcePathsImpl<V, E>(this.graph, source, paths);
    }

    @Override
    public double getPathWeight(V source, V sink) {
        GraphPath p = this.getPath(source, sink);
        if (p == null) {
            return Double.POSITIVE_INFINITY;
        }
        return p.getWeight();
    }

    protected final GraphPath<V, E> createEmptyPath(V source, V sink) {
        if (source.equals(sink)) {
            return GraphWalk.singletonWalk(this.graph, source, 0.0);
        }
        return null;
    }
}

