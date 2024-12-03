/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import org.jgrapht.GraphPath;

public interface HamiltonianCycleImprovementAlgorithm<V, E> {
    public GraphPath<V, E> improveTour(GraphPath<V, E> var1);
}

