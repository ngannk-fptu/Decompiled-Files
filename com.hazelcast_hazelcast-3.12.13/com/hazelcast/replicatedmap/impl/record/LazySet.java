/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.replicatedmap.impl.record.InternalReplicatedMapStorage;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class LazySet<K, V, R>
implements Set<R> {
    private final InternalReplicatedMapStorage<K, V> storage;
    private final IteratorFactory<K, V, R> iteratorFactory;

    LazySet(IteratorFactory<K, V, R> iteratorFactory, InternalReplicatedMapStorage<K, V> storage) {
        this.iteratorFactory = iteratorFactory;
        this.storage = storage;
    }

    @Override
    public int size() {
        return this.storage.size();
    }

    @Override
    public boolean isEmpty() {
        return this.storage.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("LazySet does not support contains requests");
    }

    @Override
    public Iterator<R> iterator() {
        Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator = this.storage.entrySet().iterator();
        return this.iteratorFactory.create(iterator);
    }

    @Override
    public Object[] toArray() {
        ArrayList<R> result = new ArrayList<R>(this.storage.values().size());
        for (R r : this) {
            result.add(r);
        }
        return result.toArray(new Object[0]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        ArrayList<R> result = new ArrayList<R>(this.storage.values().size());
        for (R r : this) {
            result.add(r);
        }
        if (a.length != result.size()) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), result.size());
        }
        for (int i = 0; i < a.length; ++i) {
            a[i] = result.get(i);
        }
        return a;
    }

    @Override
    public boolean add(R e) {
        throw new UnsupportedOperationException("LazySet is not modifiable");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("LazySet is not modifiable");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("LazySet does not support contains requests");
    }

    @Override
    public boolean addAll(Collection<? extends R> c) {
        throw new UnsupportedOperationException("LazySet is not modifiable");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("LazySet is not modifiable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("LazySet is not modifiable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("LazySet is not modifiable");
    }

    public static interface IteratorFactory<K, V, R> {
        public Iterator<R> create(Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> var1);
    }
}

