/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.graph;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.graph.AbstractUndirectedNetworkConnections;
import com.google.common.graph.EdgesConnecting;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@ElementTypesAreNonnullByDefault
final class UndirectedNetworkConnections<N, E>
extends AbstractUndirectedNetworkConnections<N, E> {
    UndirectedNetworkConnections(Map<E, N> incidentEdgeMap) {
        super(incidentEdgeMap);
    }

    static <N, E> UndirectedNetworkConnections<N, E> of() {
        return new UndirectedNetworkConnections(HashBiMap.create(2));
    }

    static <N, E> UndirectedNetworkConnections<N, E> ofImmutable(Map<E, N> incidentEdges) {
        return new UndirectedNetworkConnections<N, E>(ImmutableBiMap.copyOf(incidentEdges));
    }

    @Override
    public Set<N> adjacentNodes() {
        return Collections.unmodifiableSet(((BiMap)this.incidentEdgeMap).values());
    }

    @Override
    public Set<E> edgesConnecting(N node) {
        return new EdgesConnecting(((BiMap)this.incidentEdgeMap).inverse(), node);
    }
}

