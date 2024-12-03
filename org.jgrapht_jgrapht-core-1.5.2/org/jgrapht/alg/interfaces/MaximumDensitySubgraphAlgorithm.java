/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import org.jgrapht.Graph;

public interface MaximumDensitySubgraphAlgorithm<V, E> {
    public Graph<V, E> calculateDensest();

    public double getDensity();
}

