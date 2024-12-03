/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.common.graph.NetworkConnections;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
abstract class AbstractUndirectedNetworkConnections<N, E>
implements NetworkConnections<N, E> {
    final Map<E, N> incidentEdgeMap;

    AbstractUndirectedNetworkConnections(Map<E, N> incidentEdgeMap) {
        this.incidentEdgeMap = Preconditions.checkNotNull(incidentEdgeMap);
    }

    @Override
    public Set<N> predecessors() {
        return this.adjacentNodes();
    }

    @Override
    public Set<N> successors() {
        return this.adjacentNodes();
    }

    @Override
    public Set<E> incidentEdges() {
        return Collections.unmodifiableSet(this.incidentEdgeMap.keySet());
    }

    @Override
    public Set<E> inEdges() {
        return this.incidentEdges();
    }

    @Override
    public Set<E> outEdges() {
        return this.incidentEdges();
    }

    @Override
    public N adjacentNode(E edge) {
        return Objects.requireNonNull(this.incidentEdgeMap.get(edge));
    }

    @Override
    @CheckForNull
    public N removeInEdge(E edge, boolean isSelfLoop) {
        if (!isSelfLoop) {
            return this.removeOutEdge(edge);
        }
        return null;
    }

    @Override
    public N removeOutEdge(E edge) {
        N previousNode = this.incidentEdgeMap.remove(edge);
        return Objects.requireNonNull(previousNode);
    }

    @Override
    public void addInEdge(E edge, N node, boolean isSelfLoop) {
        if (!isSelfLoop) {
            this.addOutEdge(edge, node);
        }
    }

    @Override
    public void addOutEdge(E edge, N node) {
        N previousNode = this.incidentEdgeMap.put(edge, node);
        Preconditions.checkState(previousNode == null);
    }
}

