/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

public interface ShortestPathAlgorithm<V, E> {
    public GraphPath<V, E> getPath(V var1, V var2);

    public double getPathWeight(V var1, V var2);

    public SingleSourcePaths<V, E> getPaths(V var1);

    public static interface SingleSourcePaths<V, E> {
        public Graph<V, E> getGraph();

        public V getSourceVertex();

        public double getWeight(V var1);

        public GraphPath<V, E> getPath(V var1);
    }
}

