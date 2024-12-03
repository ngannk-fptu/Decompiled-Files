/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import org.jgrapht.Graph;
import org.jgrapht.util.TypeUtil;

class MaskEdgeSet<V, E>
extends AbstractSet<E>
implements Serializable {
    private static final long serialVersionUID = 4208908842850100708L;
    private final Graph<V, E> graph;
    private final Set<E> edgeSet;
    private final Predicate<V> vertexMask;
    private final Predicate<E> edgeMask;

    public MaskEdgeSet(Graph<V, E> graph, Set<E> edgeSet, Predicate<V> vertexMask, Predicate<E> edgeMask) {
        this.graph = graph;
        this.edgeSet = edgeSet;
        this.vertexMask = vertexMask;
        this.edgeMask = edgeMask;
    }

    @Override
    public boolean contains(Object o) {
        if (!this.edgeSet.contains(o)) {
            return false;
        }
        Object e = TypeUtil.uncheckedCast(o);
        return !this.edgeMask.test(e) && !this.vertexMask.test(this.graph.getEdgeSource(e)) && !this.vertexMask.test(this.graph.getEdgeTarget(e));
    }

    @Override
    public Iterator<E> iterator() {
        return this.edgeSet.stream().filter(e -> !this.edgeMask.test(e) && !this.vertexMask.test(this.graph.getEdgeSource(e)) && !this.vertexMask.test(this.graph.getEdgeTarget(e))).iterator();
    }

    @Override
    public int size() {
        return (int)this.edgeSet.stream().filter(e -> !this.edgeMask.test(e) && !this.vertexMask.test(this.graph.getEdgeSource(e)) && !this.vertexMask.test(this.graph.getEdgeTarget(e))).count();
    }

    @Override
    public boolean isEmpty() {
        return !this.iterator().hasNext();
    }
}

