/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class WeightedUnmodifiableSet<E>
extends AbstractSet<E>
implements Serializable {
    private static final long serialVersionUID = -5913435131882975869L;
    public final Set<E> backingSet;
    public final double weight;

    public WeightedUnmodifiableSet(Set<E> backingSet) {
        this.backingSet = backingSet;
        this.weight = backingSet.size();
    }

    public WeightedUnmodifiableSet(Set<E> backingSet, double weight) {
        this.backingSet = backingSet;
        this.weight = weight;
    }

    public double getWeight() {
        return this.weight;
    }

    @Override
    public int size() {
        return this.backingSet.size();
    }

    @Override
    public boolean isEmpty() {
        return this.backingSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.backingSet.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return this.backingSet.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.backingSet.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.backingSet.toArray(a);
    }

    @Override
    public boolean add(E v) {
        throw new UnsupportedOperationException("This set is unmodifiable");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("This set is unmodifiable");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.backingSet.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("This set is unmodifiable");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("This set is unmodifiable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("This set is unmodifiable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("This set is unmodifiable");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WeightedUnmodifiableSet)) {
            return false;
        }
        WeightedUnmodifiableSet other = (WeightedUnmodifiableSet)o;
        return this.backingSet.equals(other.backingSet);
    }

    @Override
    public int hashCode() {
        return this.backingSet.hashCode();
    }
}

