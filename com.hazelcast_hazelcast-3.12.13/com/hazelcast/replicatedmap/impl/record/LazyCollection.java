/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.record;

import com.hazelcast.replicatedmap.impl.record.InternalReplicatedMapStorage;
import com.hazelcast.replicatedmap.impl.record.LazySet;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

class LazyCollection<K, V>
implements Collection<V> {
    private final InternalReplicatedMapStorage<K, V> storage;
    private final LazySet.IteratorFactory<K, V, V> iteratorFactory;
    private final Collection<ReplicatedRecord<K, V>> values;

    public LazyCollection(LazySet.IteratorFactory<K, V, V> iteratorFactory, InternalReplicatedMapStorage<K, V> storage) {
        this.iteratorFactory = iteratorFactory;
        this.values = storage.values();
        this.storage = storage;
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Override
    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    @Override
    public boolean contains(Object value) {
        throw new UnsupportedOperationException("LazySet does not support contains requests");
    }

    @Override
    public Iterator<V> iterator() {
        Iterator<Map.Entry<K, ReplicatedRecord<K, V>>> iterator = this.storage.entrySet().iterator();
        return this.iteratorFactory.create(iterator);
    }

    @Override
    public Object[] toArray() {
        ArrayList<V> result = new ArrayList<V>(this.storage.values().size());
        Iterator<V> iterator = this.iterator();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result.toArray(new Object[0]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        ArrayList<V> result = new ArrayList<V>(this.storage.values().size());
        Iterator<V> iterator = this.iterator();
        while (iterator.hasNext()) {
            result.add(iterator.next());
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
    public boolean add(V v) {
        throw new UnsupportedOperationException("LazyList is not modifiable");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("LazyList is not modifiable");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("LazySet does not support contains requests");
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        throw new UnsupportedOperationException("LazyList is not modifiable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("LazyList is not modifiable");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("LazyList is not modifiable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("LazyList is not modifiable");
    }
}

