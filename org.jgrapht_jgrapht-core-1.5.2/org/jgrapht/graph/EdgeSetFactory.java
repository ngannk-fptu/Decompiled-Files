/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.Set;

public interface EdgeSetFactory<V, E> {
    public Set<E> createEdgeSet(V var1);
}

