/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseManyToManyShortestPaths;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;

public class DefaultManyToManyShortestPaths<V, E>
extends BaseManyToManyShortestPaths<V, E> {
    private final Function<Graph<V, E>, ShortestPathAlgorithm<V, E>> function;

    public DefaultManyToManyShortestPaths(Graph<V, E> graph) {
        this(graph, g -> new BidirectionalDijkstraShortestPath(g));
    }

    public DefaultManyToManyShortestPaths(Graph<V, E> graph, Function<Graph<V, E>, ShortestPathAlgorithm<V, E>> function) {
        super(graph);
        this.function = function;
    }

    @Override
    public ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> getManyToManyPaths(Set<V> sources, Set<V> targets) {
        Objects.requireNonNull(sources, "sources cannot be null!");
        Objects.requireNonNull(targets, "targets cannot be null!");
        ShortestPathAlgorithm<V, E> algorithm = this.function.apply(this.graph);
        HashMap pathMap = new HashMap();
        for (V source : sources) {
            pathMap.put(source, new HashMap());
        }
        for (V source : sources) {
            for (V target : targets) {
                ((Map)pathMap.get(source)).put(target, algorithm.getPath(source, target));
            }
        }
        return new DefaultManyToManyShortestPathsImpl(sources, targets, pathMap);
    }

    static class DefaultManyToManyShortestPathsImpl<V, E>
    extends ManyToManyShortestPathsAlgorithm.BaseManyToManyShortestPathsImpl<V, E> {
        private final Map<V, Map<V, GraphPath<V, E>>> pathsMap;

        DefaultManyToManyShortestPathsImpl(Set<V> sources, Set<V> targets, Map<V, Map<V, GraphPath<V, E>>> pathsMap) {
            super(sources, targets);
            this.pathsMap = pathsMap;
        }

        @Override
        public GraphPath<V, E> getPath(V source, V target) {
            this.assertCorrectSourceAndTarget(source, target);
            return this.pathsMap.get(source).get(target);
        }

        @Override
        public double getWeight(V source, V target) {
            this.assertCorrectSourceAndTarget(source, target);
            GraphPath<V, E> path = this.pathsMap.get(source).get(target);
            if (path == null) {
                return Double.POSITIVE_INFINITY;
            }
            return path.getWeight();
        }
    }
}

