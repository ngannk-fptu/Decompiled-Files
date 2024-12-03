/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraClosestFirstIterator;
import org.jgrapht.alg.shortestpath.ListSingleSourcePathsImpl;

abstract class BaseManyToManyShortestPaths<V, E>
implements ManyToManyShortestPathsAlgorithm<V, E> {
    protected final Graph<V, E> graph;

    public BaseManyToManyShortestPaths(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph is null");
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink) {
        return this.getManyToManyPaths(Collections.singleton(source), Collections.singleton(sink)).getPath(source, sink);
    }

    @Override
    public double getPathWeight(V source, V sink) {
        GraphPath<V, E> p = this.getPath(source, sink);
        if (p == null) {
            return Double.POSITIVE_INFINITY;
        }
        return p.getWeight();
    }

    @Override
    public ShortestPathAlgorithm.SingleSourcePaths<V, E> getPaths(V source) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("graph must contain the source vertex");
        }
        HashMap<V, GraphPath<V, E>> paths = new HashMap<V, GraphPath<V, E>>();
        for (V v : this.graph.vertexSet()) {
            paths.put(v, this.getPath(source, v));
        }
        return new ListSingleSourcePathsImpl<V, E>(this.graph, source, paths);
    }

    protected static <V, E> ShortestPathAlgorithm.SingleSourcePaths<V, E> getShortestPathsTree(Graph<V, E> graph, V source, Set<V> targets) {
        DijkstraClosestFirstIterator<V, E> iterator = new DijkstraClosestFirstIterator<V, E>(graph, source);
        int reachedTargets = 0;
        while (iterator.hasNext() && reachedTargets < targets.size()) {
            if (!targets.contains(iterator.next())) continue;
            ++reachedTargets;
        }
        return iterator.getPaths();
    }
}

