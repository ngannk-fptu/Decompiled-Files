/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Network;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@ElementTypesAreNonnullByDefault
@Beta
public interface MutableNetwork<N, E>
extends Network<N, E> {
    @CanIgnoreReturnValue
    public boolean addNode(N var1);

    @CanIgnoreReturnValue
    public boolean addEdge(N var1, N var2, E var3);

    @CanIgnoreReturnValue
    public boolean addEdge(EndpointPair<N> var1, E var2);

    @CanIgnoreReturnValue
    public boolean removeNode(N var1);

    @CanIgnoreReturnValue
    public boolean removeEdge(E var1);
}

