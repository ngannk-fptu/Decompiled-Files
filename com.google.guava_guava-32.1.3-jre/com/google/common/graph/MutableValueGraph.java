/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ValueGraph;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@Beta
public interface MutableValueGraph<N, V>
extends ValueGraph<N, V> {
    @CanIgnoreReturnValue
    public boolean addNode(N var1);

    @CheckForNull
    @CanIgnoreReturnValue
    public V putEdgeValue(N var1, N var2, V var3);

    @CheckForNull
    @CanIgnoreReturnValue
    public V putEdgeValue(EndpointPair<N> var1, V var2);

    @CanIgnoreReturnValue
    public boolean removeNode(N var1);

    @CheckForNull
    @CanIgnoreReturnValue
    public V removeEdge(N var1, N var2);

    @CheckForNull
    @CanIgnoreReturnValue
    public V removeEdge(EndpointPair<N> var1);
}

