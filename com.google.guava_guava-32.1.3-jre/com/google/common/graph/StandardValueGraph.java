/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.graph.AbstractGraphBuilder;
import com.google.common.graph.AbstractValueGraph;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphConnections;
import com.google.common.graph.Graphs;
import com.google.common.graph.IncidentEdgeSet;
import com.google.common.graph.MapIteratorCache;
import com.google.common.graph.MapRetrievalCache;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
class StandardValueGraph<N, V>
extends AbstractValueGraph<N, V> {
    private final boolean isDirected;
    private final boolean allowsSelfLoops;
    private final ElementOrder<N> nodeOrder;
    final MapIteratorCache<N, GraphConnections<N, V>> nodeConnections;
    long edgeCount;

    StandardValueGraph(AbstractGraphBuilder<? super N> builder) {
        this(builder, builder.nodeOrder.createMap(builder.expectedNodeCount.or(10)), 0L);
    }

    StandardValueGraph(AbstractGraphBuilder<? super N> builder, Map<N, GraphConnections<N, V>> nodeConnections, long edgeCount) {
        this.isDirected = builder.directed;
        this.allowsSelfLoops = builder.allowsSelfLoops;
        this.nodeOrder = builder.nodeOrder.cast();
        this.nodeConnections = nodeConnections instanceof TreeMap ? new MapRetrievalCache<N, GraphConnections<N, V>>(nodeConnections) : new MapIteratorCache<N, GraphConnections<N, V>>(nodeConnections);
        this.edgeCount = Graphs.checkNonNegative(edgeCount);
    }

    @Override
    public Set<N> nodes() {
        return this.nodeConnections.unmodifiableKeySet();
    }

    @Override
    public boolean isDirected() {
        return this.isDirected;
    }

    @Override
    public boolean allowsSelfLoops() {
        return this.allowsSelfLoops;
    }

    @Override
    public ElementOrder<N> nodeOrder() {
        return this.nodeOrder;
    }

    @Override
    public Set<N> adjacentNodes(N node) {
        return this.checkedConnections(node).adjacentNodes();
    }

    @Override
    public Set<N> predecessors(N node) {
        return this.checkedConnections(node).predecessors();
    }

    @Override
    public Set<N> successors(N node) {
        return this.checkedConnections(node).successors();
    }

    @Override
    public Set<EndpointPair<N>> incidentEdges(N node) {
        final GraphConnections<N, V> connections = this.checkedConnections(node);
        return new IncidentEdgeSet<N>(this, this, node){

            @Override
            public Iterator<EndpointPair<N>> iterator() {
                return connections.incidentEdgeIterator(this.node);
            }
        };
    }

    @Override
    public boolean hasEdgeConnecting(N nodeU, N nodeV) {
        return this.hasEdgeConnectingInternal(Preconditions.checkNotNull(nodeU), Preconditions.checkNotNull(nodeV));
    }

    @Override
    public boolean hasEdgeConnecting(EndpointPair<N> endpoints) {
        Preconditions.checkNotNull(endpoints);
        return this.isOrderingCompatible(endpoints) && this.hasEdgeConnectingInternal(endpoints.nodeU(), endpoints.nodeV());
    }

    @Override
    @CheckForNull
    public V edgeValueOrDefault(N nodeU, N nodeV, @CheckForNull V defaultValue) {
        return this.edgeValueOrDefaultInternal(Preconditions.checkNotNull(nodeU), Preconditions.checkNotNull(nodeV), defaultValue);
    }

    @Override
    @CheckForNull
    public V edgeValueOrDefault(EndpointPair<N> endpoints, @CheckForNull V defaultValue) {
        this.validateEndpoints(endpoints);
        return this.edgeValueOrDefaultInternal(endpoints.nodeU(), endpoints.nodeV(), defaultValue);
    }

    @Override
    protected long edgeCount() {
        return this.edgeCount;
    }

    private final GraphConnections<N, V> checkedConnections(N node) {
        GraphConnections<N, V> connections = this.nodeConnections.get(node);
        if (connections == null) {
            Preconditions.checkNotNull(node);
            throw new IllegalArgumentException("Node " + node + " is not an element of this graph.");
        }
        return connections;
    }

    final boolean containsNode(@CheckForNull N node) {
        return this.nodeConnections.containsKey(node);
    }

    private final boolean hasEdgeConnectingInternal(N nodeU, N nodeV) {
        GraphConnections<N, V> connectionsU = this.nodeConnections.get(nodeU);
        return connectionsU != null && connectionsU.successors().contains(nodeV);
    }

    @CheckForNull
    private final V edgeValueOrDefaultInternal(N nodeU, N nodeV, @CheckForNull V defaultValue) {
        V value;
        GraphConnections<N, V> connectionsU = this.nodeConnections.get(nodeU);
        V v = value = connectionsU == null ? null : (V)connectionsU.value(nodeV);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}

