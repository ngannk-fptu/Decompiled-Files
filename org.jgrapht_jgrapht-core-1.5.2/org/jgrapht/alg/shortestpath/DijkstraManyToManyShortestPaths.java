/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseManyToManyShortestPaths;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.GraphWalk;

public class DijkstraManyToManyShortestPaths<V, E>
extends BaseManyToManyShortestPaths<V, E> {
    public DijkstraManyToManyShortestPaths(Graph<V, E> graph) {
        super(graph);
    }

    @Override
    public ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<V, E> getManyToManyPaths(Set<V> sources, Set<V> targets) {
        Objects.requireNonNull(sources, "sources cannot be null!");
        Objects.requireNonNull(targets, "targets cannot be null!");
        HashMap searchSpaces = new HashMap();
        if (sources.size() >= targets.size()) {
            for (V source : sources) {
                searchSpaces.put(source, DijkstraManyToManyShortestPaths.getShortestPathsTree(this.graph, source, targets));
            }
            return new DijkstraManyToManyShortestPathsImpl(sources, targets, false, searchSpaces);
        }
        EdgeReversedGraph edgeReversedGraph = new EdgeReversedGraph(this.graph);
        for (V target : targets) {
            searchSpaces.put(target, DijkstraManyToManyShortestPaths.getShortestPathsTree(edgeReversedGraph, target, sources));
        }
        return new DijkstraManyToManyShortestPathsImpl(sources, targets, true, searchSpaces);
    }

    private class DijkstraManyToManyShortestPathsImpl
    extends ManyToManyShortestPathsAlgorithm.BaseManyToManyShortestPathsImpl<V, E> {
        private boolean reversed;
        private final Map<V, ShortestPathAlgorithm.SingleSourcePaths<V, E>> searchSpaces;

        DijkstraManyToManyShortestPathsImpl(Set<V> sources, Set<V> targets, boolean reversed, Map<V, ShortestPathAlgorithm.SingleSourcePaths<V, E>> searchSpaces) {
            super(sources, targets);
            this.reversed = reversed;
            this.searchSpaces = searchSpaces;
        }

        @Override
        public GraphPath<V, E> getPath(V source, V target) {
            this.assertCorrectSourceAndTarget(source, target);
            if (this.reversed) {
                GraphPath reversedPath = this.searchSpaces.get(target).getPath(source);
                if (reversedPath == null) {
                    return null;
                }
                List vertices = reversedPath.getVertexList();
                List edges = reversedPath.getEdgeList();
                Collections.reverse(vertices);
                Collections.reverse(edges);
                return new GraphWalk(DijkstraManyToManyShortestPaths.this.graph, source, target, vertices, edges, reversedPath.getWeight());
            }
            return this.searchSpaces.get(source).getPath(target);
        }

        @Override
        public double getWeight(V source, V target) {
            this.assertCorrectSourceAndTarget(source, target);
            if (this.reversed) {
                return this.searchSpaces.get(target).getWeight(source);
            }
            return this.searchSpaces.get(source).getWeight(target);
        }
    }
}

