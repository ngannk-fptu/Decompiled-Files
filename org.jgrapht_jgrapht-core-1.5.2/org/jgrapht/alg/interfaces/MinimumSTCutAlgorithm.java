/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.Set;

public interface MinimumSTCutAlgorithm<V, E> {
    public double calculateMinCut(V var1, V var2);

    public double getCutCapacity();

    public Set<V> getSourcePartition();

    public Set<V> getSinkPartition();

    public Set<E> getCutEdges();
}

