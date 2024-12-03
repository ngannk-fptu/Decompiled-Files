/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.MultiObjectiveShortestPathAlgorithm;
import org.jgrapht.graph.GraphWalk;

public class ListMultiObjectiveSingleSourcePathsImpl<V, E>
implements MultiObjectiveShortestPathAlgorithm.MultiObjectiveSingleSourcePaths<V, E>,
Serializable {
    private static final long serialVersionUID = -6213225353391554721L;
    protected Graph<V, E> graph;
    protected V source;
    protected Map<V, List<GraphPath<V, E>>> paths;

    public ListMultiObjectiveSingleSourcePathsImpl(Graph<V, E> graph, V source, Map<V, List<GraphPath<V, E>>> paths) {
        this.graph = Objects.requireNonNull(graph, "Graph is null");
        this.source = Objects.requireNonNull(source, "Source vertex is null");
        this.paths = Objects.requireNonNull(paths, "Paths are null");
    }

    @Override
    public Graph<V, E> getGraph() {
        return this.graph;
    }

    @Override
    public V getSourceVertex() {
        return this.source;
    }

    @Override
    public List<GraphPath<V, E>> getPaths(V targetVertex) {
        List<GraphPath<V, E>> p = this.paths.get(targetVertex);
        if (p == null) {
            if (this.source.equals(targetVertex)) {
                return Collections.singletonList(GraphWalk.singletonWalk(this.graph, this.source, 0.0));
            }
            return Collections.emptyList();
        }
        return p;
    }
}

