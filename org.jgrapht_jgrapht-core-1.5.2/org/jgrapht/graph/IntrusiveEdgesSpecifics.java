/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.Set;

public interface IntrusiveEdgesSpecifics<V, E>
extends Serializable {
    public V getEdgeSource(E var1);

    public V getEdgeTarget(E var1);

    public boolean add(E var1, V var2, V var3);

    public boolean containsEdge(E var1);

    public Set<E> getEdgeSet();

    public void remove(E var1);

    public double getEdgeWeight(E var1);

    public void setEdgeWeight(E var1, double var2);
}

