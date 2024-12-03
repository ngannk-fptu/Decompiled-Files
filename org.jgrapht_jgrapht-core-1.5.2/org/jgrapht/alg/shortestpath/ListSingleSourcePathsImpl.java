/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.graph.GraphWalk;

public class ListSingleSourcePathsImpl<V, E>
implements ShortestPathAlgorithm.SingleSourcePaths<V, E>,
Serializable {
    private static final long serialVersionUID = -60070018446561686L;
    protected Graph<V, E> graph;
    protected V source;
    protected Map<V, GraphPath<V, E>> paths;

    public ListSingleSourcePathsImpl(Graph<V, E> graph, V source, Map<V, GraphPath<V, E>> paths) {
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
    public double getWeight(V targetVertex) {
        GraphPath<V, E> p = this.paths.get(targetVertex);
        if (p == null) {
            if (this.source.equals(targetVertex)) {
                return 0.0;
            }
            return Double.POSITIVE_INFINITY;
        }
        return p.getWeight();
    }

    @Override
    public GraphPath<V, E> getPath(V targetVertex) {
        GraphPath<V, E> p = this.paths.get(targetVertex);
        if (p == null) {
            if (this.source.equals(targetVertex)) {
                return GraphWalk.singletonWalk(this.graph, this.source, 0.0);
            }
            return null;
        }
        return p;
    }
}

