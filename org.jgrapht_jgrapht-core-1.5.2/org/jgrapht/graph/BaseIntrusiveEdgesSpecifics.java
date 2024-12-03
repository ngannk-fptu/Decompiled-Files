/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.graph.IntrusiveEdge;
import org.jgrapht.graph.IntrusiveEdgeException;
import org.jgrapht.util.TypeUtil;

public abstract class BaseIntrusiveEdgesSpecifics<V, E, IE extends IntrusiveEdge>
implements Serializable {
    private static final long serialVersionUID = -7498268216742485L;
    protected Map<E, IE> edgeMap;
    protected transient Set<E> unmodifiableEdgeSet = null;

    public BaseIntrusiveEdgesSpecifics(Map<E, IE> edgeMap) {
        this.edgeMap = Objects.requireNonNull(edgeMap);
    }

    public boolean containsEdge(E e) {
        return this.edgeMap.containsKey(e);
    }

    public Set<E> getEdgeSet() {
        if (this.unmodifiableEdgeSet == null) {
            this.unmodifiableEdgeSet = Collections.unmodifiableSet(this.edgeMap.keySet());
        }
        return this.unmodifiableEdgeSet;
    }

    public void remove(E e) {
        this.edgeMap.remove(e);
    }

    public V getEdgeSource(E e) {
        IE ie = this.getIntrusiveEdge(e);
        if (ie == null) {
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        }
        return (V)TypeUtil.uncheckedCast(((IntrusiveEdge)ie).source);
    }

    public V getEdgeTarget(E e) {
        IE ie = this.getIntrusiveEdge(e);
        if (ie == null) {
            throw new IllegalArgumentException("no such edge in graph: " + e.toString());
        }
        return (V)TypeUtil.uncheckedCast(((IntrusiveEdge)ie).target);
    }

    public double getEdgeWeight(E e) {
        return 1.0;
    }

    public void setEdgeWeight(E e, double weight) {
        throw new UnsupportedOperationException();
    }

    public abstract boolean add(E var1, V var2, V var3);

    protected boolean addIntrusiveEdge(E edge, V sourceVertex, V targetVertex, IE e) {
        if (((IntrusiveEdge)e).source == null && ((IntrusiveEdge)e).target == null) {
            ((IntrusiveEdge)e).source = sourceVertex;
            ((IntrusiveEdge)e).target = targetVertex;
        } else if (((IntrusiveEdge)e).source != sourceVertex || ((IntrusiveEdge)e).target != targetVertex) {
            throw new IntrusiveEdgeException(((IntrusiveEdge)e).source, ((IntrusiveEdge)e).target);
        }
        return this.edgeMap.putIfAbsent(edge, e) == null;
    }

    protected abstract IE getIntrusiveEdge(E var1);
}

