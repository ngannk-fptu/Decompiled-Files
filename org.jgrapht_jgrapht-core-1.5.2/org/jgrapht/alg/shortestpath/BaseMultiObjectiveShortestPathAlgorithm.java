/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.HashMap;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.MultiObjectiveShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.ListMultiObjectiveSingleSourcePathsImpl;
import org.jgrapht.graph.GraphWalk;

abstract class BaseMultiObjectiveShortestPathAlgorithm<V, E>
implements MultiObjectiveShortestPathAlgorithm<V, E> {
    static final String GRAPH_MUST_CONTAIN_THE_SOURCE_VERTEX = "Graph must contain the source vertex!";
    static final String GRAPH_MUST_CONTAIN_THE_SINK_VERTEX = "Graph must contain the sink vertex!";
    protected final Graph<V, E> graph;

    public BaseMultiObjectiveShortestPathAlgorithm(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph is null");
    }

    @Override
    public MultiObjectiveShortestPathAlgorithm.MultiObjectiveSingleSourcePaths<V, E> getPaths(V source) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException(GRAPH_MUST_CONTAIN_THE_SOURCE_VERTEX);
        }
        HashMap paths = new HashMap();
        for (V v : this.graph.vertexSet()) {
            paths.put(v, this.getPaths(source, v));
        }
        return new ListMultiObjectiveSingleSourcePathsImpl<V, E>(this.graph, source, paths);
    }

    protected final GraphPath<V, E> createEmptyPath(V source, V sink) {
        if (source.equals(sink)) {
            return GraphWalk.singletonWalk(this.graph, source, 0.0);
        }
        return null;
    }
}

