/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.Map;
import org.jgrapht.graph.BaseIntrusiveEdgesSpecifics;
import org.jgrapht.graph.IntrusiveEdge;
import org.jgrapht.graph.IntrusiveEdgesSpecifics;

public class UniformIntrusiveEdgesSpecifics<V, E>
extends BaseIntrusiveEdgesSpecifics<V, E, IntrusiveEdge>
implements IntrusiveEdgesSpecifics<V, E> {
    private static final long serialVersionUID = -5736320893697031114L;

    public UniformIntrusiveEdgesSpecifics(Map<E, IntrusiveEdge> map) {
        super(map);
    }

    @Override
    public boolean add(E e, V sourceVertex, V targetVertex) {
        if (e instanceof IntrusiveEdge) {
            return this.addIntrusiveEdge(e, sourceVertex, targetVertex, (IntrusiveEdge)e);
        }
        int previousSize = this.edgeMap.size();
        IntrusiveEdge intrusiveEdge = this.edgeMap.computeIfAbsent(e, i -> new IntrusiveEdge());
        if (previousSize < this.edgeMap.size()) {
            intrusiveEdge.source = sourceVertex;
            intrusiveEdge.target = targetVertex;
            return true;
        }
        return false;
    }

    @Override
    protected IntrusiveEdge getIntrusiveEdge(E e) {
        if (e instanceof IntrusiveEdge) {
            return (IntrusiveEdge)e;
        }
        return (IntrusiveEdge)this.edgeMap.get(e);
    }
}

