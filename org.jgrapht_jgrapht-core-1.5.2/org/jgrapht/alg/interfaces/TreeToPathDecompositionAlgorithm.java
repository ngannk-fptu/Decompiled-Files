/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.util.ArrayUnenforcedSet;

public interface TreeToPathDecompositionAlgorithm<V, E> {
    public PathDecomposition<V, E> getPathDecomposition();

    public static class PathDecompositionImpl<V, E>
    implements PathDecomposition<V, E>,
    Serializable {
        private static final long serialVersionUID = 8468626434814461297L;
        private final Set<E> edges;
        private final Set<GraphPath<V, E>> paths;

        public PathDecompositionImpl(Graph<V, E> graph, Set<E> edges, List<List<V>> paths) {
            this.edges = edges;
            Set arrayUnenforcedSet = paths.stream().map(path -> new GraphWalk(graph, path, path.size())).collect(Collectors.toCollection(ArrayUnenforcedSet::new));
            this.paths = Collections.unmodifiableSet(arrayUnenforcedSet);
        }

        @Override
        public Set<E> getEdges() {
            return this.edges;
        }

        @Override
        public Set<GraphPath<V, E>> getPaths() {
            return this.paths;
        }

        public String toString() {
            return "Path-Decomposition [edges=" + this.edges + ",paths=" + this.getPaths() + "]";
        }
    }

    public static interface PathDecomposition<V, E> {
        public Set<E> getEdges();

        public Set<GraphPath<V, E>> getPaths();

        default public int numberOfPaths() {
            return this.getPaths().size();
        }
    }
}

