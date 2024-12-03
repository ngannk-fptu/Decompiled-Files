/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.Map;
import org.jgrapht.graph.BaseIntrusiveEdgesSpecifics;
import org.jgrapht.graph.IntrusiveEdge;
import org.jgrapht.graph.IntrusiveEdgesSpecifics;
import org.jgrapht.graph.IntrusiveWeightedEdge;

public class WeightedIntrusiveEdgesSpecifics<V, E>
extends BaseIntrusiveEdgesSpecifics<V, E, IntrusiveWeightedEdge>
implements IntrusiveEdgesSpecifics<V, E> {
    private static final long serialVersionUID = 5327226615635500554L;

    public WeightedIntrusiveEdgesSpecifics(Map<E, IntrusiveWeightedEdge> map) {
        super(map);
    }

    @Override
    public boolean add(E e, V sourceVertex, V targetVertex) {
        if (e instanceof IntrusiveWeightedEdge) {
            return this.addIntrusiveEdge(e, sourceVertex, targetVertex, (IntrusiveWeightedEdge)e);
        }
        int previousSize = this.edgeMap.size();
        IntrusiveWeightedEdge intrusiveEdge = this.edgeMap.computeIfAbsent(e, i -> new IntrusiveWeightedEdge());
        if (previousSize < this.edgeMap.size()) {
            intrusiveEdge.source = sourceVertex;
            intrusiveEdge.target = targetVertex;
            return true;
        }
        return false;
    }

    @Override
    public double getEdgeWeight(E e) {
        IntrusiveEdge ie = this.getIntrusiveEdge((Object)e);
        if (ie == null) {
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        }
        return ((IntrusiveWeightedEdge)ie).weight;
    }

    @Override
    public void setEdgeWeight(E e, double weight) {
        IntrusiveEdge ie = this.getIntrusiveEdge((Object)e);
        if (ie == null) {
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        }
        ((IntrusiveWeightedEdge)ie).weight = weight;
    }

    @Override
    protected IntrusiveWeightedEdge getIntrusiveEdge(E e) {
        if (e instanceof IntrusiveWeightedEdge) {
            return (IntrusiveWeightedEdge)e;
        }
        return (IntrusiveWeightedEdge)this.edgeMap.get(e);
    }
}

