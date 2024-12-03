/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

public interface MultiObjectiveShortestPathAlgorithm<V, E> {
    public List<GraphPath<V, E>> getPaths(V var1, V var2);

    public MultiObjectiveSingleSourcePaths<V, E> getPaths(V var1);

    public static interface MultiObjectiveSingleSourcePaths<V, E> {
        public Graph<V, E> getGraph();

        public V getSourceVertex();

        public List<GraphPath<V, E>> getPaths(V var1);
    }
}

