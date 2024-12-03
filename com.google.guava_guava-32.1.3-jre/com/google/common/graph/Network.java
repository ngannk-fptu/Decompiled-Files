/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.DoNotMock
 *  javax.annotation.CheckForNull
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.PredecessorsFunction;
import com.google.common.graph.SuccessorsFunction;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Optional;
import java.util.Set;
import javax.annotation.CheckForNull;

@DoNotMock(value="Use NetworkBuilder to create a real instance")
@ElementTypesAreNonnullByDefault
@Beta
public interface Network<N, E>
extends SuccessorsFunction<N>,
PredecessorsFunction<N> {
    public Set<N> nodes();

    public Set<E> edges();

    public Graph<N> asGraph();

    public boolean isDirected();

    public boolean allowsParallelEdges();

    public boolean allowsSelfLoops();

    public ElementOrder<N> nodeOrder();

    public ElementOrder<E> edgeOrder();

    public Set<N> adjacentNodes(N var1);

    @Override
    public Set<N> predecessors(N var1);

    @Override
    public Set<N> successors(N var1);

    public Set<E> incidentEdges(N var1);

    public Set<E> inEdges(N var1);

    public Set<E> outEdges(N var1);

    public int degree(N var1);

    public int inDegree(N var1);

    public int outDegree(N var1);

    public EndpointPair<N> incidentNodes(E var1);

    public Set<E> adjacentEdges(E var1);

    public Set<E> edgesConnecting(N var1, N var2);

    public Set<E> edgesConnecting(EndpointPair<N> var1);

    public Optional<E> edgeConnecting(N var1, N var2);

    public Optional<E> edgeConnecting(EndpointPair<N> var1);

    @CheckForNull
    public E edgeConnectingOrNull(N var1, N var2);

    @CheckForNull
    public E edgeConnectingOrNull(EndpointPair<N> var1);

    public boolean hasEdgeConnecting(N var1, N var2);

    public boolean hasEdgeConnecting(EndpointPair<N> var1);

    public boolean equals(@CheckForNull Object var1);

    public int hashCode();
}

