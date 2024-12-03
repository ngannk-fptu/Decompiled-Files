/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.GraphOrdering;
import org.jgrapht.alg.isomorphism.IsomorphicGraphMapping;
import org.jgrapht.alg.isomorphism.VF2State;

abstract class VF2MappingIterator<V, E>
implements Iterator<GraphMapping<V, E>> {
    protected Comparator<V> vertexComparator;
    protected Comparator<E> edgeComparator;
    protected IsomorphicGraphMapping<V, E> nextMapping;
    protected Boolean hadOneMapping;
    protected GraphOrdering<V, E> ordering1;
    protected GraphOrdering<V, E> ordering2;
    protected ArrayDeque<VF2State<V, E>> stateStack;

    public VF2MappingIterator(GraphOrdering<V, E> ordering1, GraphOrdering<V, E> ordering2, Comparator<V> vertexComparator, Comparator<E> edgeComparator) {
        this.ordering1 = ordering1;
        this.ordering2 = ordering2;
        this.vertexComparator = vertexComparator;
        this.edgeComparator = edgeComparator;
        this.stateStack = new ArrayDeque();
    }

    protected abstract IsomorphicGraphMapping<V, E> match();

    protected IsomorphicGraphMapping<V, E> matchAndCheck() {
        IsomorphicGraphMapping<V, E> rel = this.match();
        if (rel != null) {
            this.hadOneMapping = true;
        }
        return rel;
    }

    @Override
    public boolean hasNext() {
        return this.nextMapping != null || (this.nextMapping = this.matchAndCheck()) != null;
    }

    @Override
    public IsomorphicGraphMapping<V, E> next() {
        if (this.nextMapping != null) {
            IsomorphicGraphMapping<V, E> tmp = this.nextMapping;
            this.nextMapping = null;
            return tmp;
        }
        IsomorphicGraphMapping<V, E> rel = this.matchAndCheck();
        if (rel == null) {
            throw new NoSuchElementException();
        }
        return rel;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

