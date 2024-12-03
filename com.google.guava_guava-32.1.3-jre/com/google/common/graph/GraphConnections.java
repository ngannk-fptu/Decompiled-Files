/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 */
package com.google.common.graph;

import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.common.graph.EndpointPair;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
interface GraphConnections<N, V> {
    public Set<N> adjacentNodes();

    public Set<N> predecessors();

    public Set<N> successors();

    public Iterator<EndpointPair<N>> incidentEdgeIterator(N var1);

    @CheckForNull
    public V value(N var1);

    public void removePredecessor(N var1);

    @CheckForNull
    @CanIgnoreReturnValue
    public V removeSuccessor(N var1);

    public void addPredecessor(N var1, V var2);

    @CheckForNull
    @CanIgnoreReturnValue
    public V addSuccessor(N var1, V var2);
}

