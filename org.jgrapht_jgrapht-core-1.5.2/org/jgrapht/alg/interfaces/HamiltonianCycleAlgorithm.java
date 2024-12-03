/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

public interface HamiltonianCycleAlgorithm<V, E> {
    public GraphPath<V, E> getTour(Graph<V, E> var1);
}

