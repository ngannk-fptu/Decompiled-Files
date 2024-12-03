/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.graph;

import com.google.common.graph.BaseGraph;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.common.graph.EndpointPair;
import java.util.AbstractSet;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
abstract class IncidentEdgeSet<N>
extends AbstractSet<EndpointPair<N>> {
    final N node;
    final BaseGraph<N> graph;

    IncidentEdgeSet(BaseGraph<N> graph, N node) {
        this.graph = graph;
        this.node = node;
    }

    @Override
    public boolean remove(@CheckForNull Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        if (this.graph.isDirected()) {
            return this.graph.inDegree(this.node) + this.graph.outDegree(this.node) - (this.graph.successors((Object)this.node).contains(this.node) ? 1 : 0);
        }
        return this.graph.adjacentNodes(this.node).size();
    }

    @Override
    public boolean contains(@CheckForNull Object obj) {
        if (!(obj instanceof EndpointPair)) {
            return false;
        }
        EndpointPair endpointPair = (EndpointPair)obj;
        if (this.graph.isDirected()) {
            if (!endpointPair.isOrdered()) {
                return false;
            }
            Object source = endpointPair.source();
            Object target = endpointPair.target();
            return this.node.equals(source) && this.graph.successors((Object)this.node).contains(target) || this.node.equals(target) && this.graph.predecessors((Object)this.node).contains(source);
        }
        if (endpointPair.isOrdered()) {
            return false;
        }
        Set<N> adjacent = this.graph.adjacentNodes(this.node);
        Object nodeU = endpointPair.nodeU();
        Object nodeV = endpointPair.nodeV();
        return this.node.equals(nodeV) && adjacent.contains(nodeU) || this.node.equals(nodeU) && adjacent.contains(nodeV);
    }
}

