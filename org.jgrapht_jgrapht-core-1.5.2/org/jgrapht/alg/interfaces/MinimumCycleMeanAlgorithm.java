/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import org.jgrapht.GraphPath;

public interface MinimumCycleMeanAlgorithm<V, E> {
    public double getCycleMean();

    public GraphPath<V, E> getCycle();
}

