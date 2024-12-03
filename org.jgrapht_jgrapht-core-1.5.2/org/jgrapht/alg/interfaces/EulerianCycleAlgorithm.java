/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

public interface EulerianCycleAlgorithm<V, E> {
    public GraphPath<V, E> getEulerianCycle(Graph<V, E> var1);
}

