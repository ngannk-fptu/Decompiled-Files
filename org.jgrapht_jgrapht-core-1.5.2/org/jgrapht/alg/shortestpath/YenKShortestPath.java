/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.PathValidator;
import org.jgrapht.alg.shortestpath.YenShortestPathIterator;

public class YenKShortestPath<V, E>
implements KShortestPathAlgorithm<V, E> {
    private final Graph<V, E> graph;
    private PathValidator<V, E> pathValidator;

    public YenKShortestPath(Graph<V, E> graph) {
        this(graph, null);
    }

    public YenKShortestPath(Graph<V, E> graph, PathValidator<V, E> pathValidator) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null!");
        this.pathValidator = pathValidator;
    }

    @Override
    public List<GraphPath<V, E>> getPaths(V source, V sink, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k should be positive");
        }
        ArrayList<GraphPath<V, Object>> result = new ArrayList<GraphPath<V, Object>>();
        YenShortestPathIterator<V, E> iterator = new YenShortestPathIterator<V, E>(this.graph, source, sink, this.pathValidator);
        for (int i = 0; i < k && iterator.hasNext(); ++i) {
            result.add((GraphPath<V, Object>)iterator.next());
        }
        return result;
    }
}

