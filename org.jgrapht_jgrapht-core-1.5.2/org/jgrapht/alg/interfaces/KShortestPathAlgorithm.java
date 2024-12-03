/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.List;
import org.jgrapht.GraphPath;

public interface KShortestPathAlgorithm<V, E> {
    public List<GraphPath<V, E>> getPaths(V var1, V var2, int var3);
}

