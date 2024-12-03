/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.DoNotMock
 *  javax.annotation.CheckForNull
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.graph.BaseGraph;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.common.graph.EndpointPair;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Set;
import javax.annotation.CheckForNull;

@DoNotMock(value="Use GraphBuilder to create a real instance")
@ElementTypesAreNonnullByDefault
@Beta
public interface Graph<N>
extends BaseGraph<N> {
    @Override
    public Set<N> nodes();

    @Override
    public Set<EndpointPair<N>> edges();

    @Override
    public boolean isDirected();

    @Override
    public boolean allowsSelfLoops();

    @Override
    public ElementOrder<N> nodeOrder();

    @Override
    public ElementOrder<N> incidentEdgeOrder();

    @Override
    public Set<N> adjacentNodes(N var1);

    @Override
    public Set<N> predecessors(N var1);

    @Override
    public Set<N> successors(N var1);

    @Override
    public Set<EndpointPair<N>> incidentEdges(N var1);

    @Override
    public int degree(N var1);

    @Override
    public int inDegree(N var1);

    @Override
    public int outDegree(N var1);

    @Override
    public boolean hasEdgeConnecting(N var1, N var2);

    @Override
    public boolean hasEdgeConnecting(EndpointPair<N> var1);

    public boolean equals(@CheckForNull Object var1);

    public int hashCode();
}

