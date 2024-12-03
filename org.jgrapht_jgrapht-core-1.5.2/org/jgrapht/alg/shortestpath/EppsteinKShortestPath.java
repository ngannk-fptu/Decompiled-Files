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
import org.jgrapht.alg.shortestpath.EppsteinShortestPathIterator;

public class EppsteinKShortestPath<V, E>
implements KShortestPathAlgorithm<V, E> {
    private final Graph<V, E> graph;

    public EppsteinKShortestPath(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null!");
    }

    @Override
    public List<GraphPath<V, E>> getPaths(V source, V sink, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k must be non-negative");
        }
        ArrayList<GraphPath<V, Object>> result = new ArrayList<GraphPath<V, Object>>();
        EppsteinShortestPathIterator<V, E> iterator = new EppsteinShortestPathIterator<V, E>(this.graph, source, sink);
        for (int i = 0; i < k && iterator.hasNext(); ++i) {
            result.add((GraphPath<V, Object>)iterator.next());
        }
        return result;
    }
}

