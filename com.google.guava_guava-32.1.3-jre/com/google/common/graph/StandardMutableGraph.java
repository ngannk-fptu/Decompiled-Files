/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.graph;

import com.google.common.graph.AbstractGraphBuilder;
import com.google.common.graph.BaseGraph;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ForwardingGraph;
import com.google.common.graph.GraphConstants;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.StandardMutableValueGraph;

@ElementTypesAreNonnullByDefault
final class StandardMutableGraph<N>
extends ForwardingGraph<N>
implements MutableGraph<N> {
    private final MutableValueGraph<N, GraphConstants.Presence> backingValueGraph;

    StandardMutableGraph(AbstractGraphBuilder<? super N> builder) {
        this.backingValueGraph = new StandardMutableValueGraph<N, GraphConstants.Presence>(builder);
    }

    @Override
    BaseGraph<N> delegate() {
        return this.backingValueGraph;
    }

    @Override
    public boolean addNode(N node) {
        return this.backingValueGraph.addNode(node);
    }

    @Override
    public boolean putEdge(N nodeU, N nodeV) {
        return this.backingValueGraph.putEdgeValue(nodeU, nodeV, GraphConstants.Presence.EDGE_EXISTS) == null;
    }

    @Override
    public boolean putEdge(EndpointPair<N> endpoints) {
        this.validateEndpoints(endpoints);
        return this.putEdge(endpoints.nodeU(), endpoints.nodeV());
    }

    @Override
    public boolean removeNode(N node) {
        return this.backingValueGraph.removeNode(node);
    }

    @Override
    public boolean removeEdge(N nodeU, N nodeV) {
        return this.backingValueGraph.removeEdge(nodeU, nodeV) != null;
    }

    @Override
    public boolean removeEdge(EndpointPair<N> endpoints) {
        this.validateEndpoints(endpoints);
        return this.removeEdge(endpoints.nodeU(), endpoints.nodeV());
    }
}

