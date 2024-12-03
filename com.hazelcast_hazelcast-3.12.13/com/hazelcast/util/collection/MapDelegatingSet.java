/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.collection;

import com.hazelcast.util.function.Predicate;
import com.hazelcast.util.function.Supplier;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

public final class MapDelegatingSet<V>
extends AbstractSet<V> {
    private final Map<?, ?> delegate;
    private final Supplier<Iterator<V>> iterator;
    private final Predicate contains;

    public MapDelegatingSet(Map<?, ?> delegate, Supplier<Iterator<V>> iterator, Predicate contains) {
        this.delegate = delegate;
        this.iterator = iterator;
        this.contains = contains;
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.contains.test(o);
    }

    @Override
    public Iterator<V> iterator() {
        return this.iterator.get();
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }
}

