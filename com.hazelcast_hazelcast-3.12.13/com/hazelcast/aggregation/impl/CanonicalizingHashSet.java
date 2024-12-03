/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aggregation.impl;

import com.hazelcast.aggregation.impl.AggregatorDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.impl.Comparables;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public final class CanonicalizingHashSet<E>
implements Set<E>,
IdentifiedDataSerializable {
    private HashMap<Object, E> map;

    public CanonicalizingHashSet() {
        this.map = new HashMap();
    }

    public CanonicalizingHashSet(int capacity) {
        this.map = new HashMap(capacity);
    }

    void addAllInternal(CanonicalizingHashSet<E> set) {
        this.map.putAll(set.map);
    }

    void addInternal(E e) {
        this.map.put(CanonicalizingHashSet.canonicalize(e), e);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.size());
        for (E element : this) {
            out.writeObject(element);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int count = in.readInt();
        this.map = new HashMap(MapUtil.calculateInitialCapacity(count));
        for (int i = 0; i < count; ++i) {
            Object element = in.readObject();
            this.addInternal(element);
        }
    }

    @Override
    public int getFactoryId() {
        return AggregatorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 19;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.map.containsKey(CanonicalizingHashSet.canonicalize(o));
    }

    @Override
    public Iterator<E> iterator() {
        return this.map.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.map.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.map.values().toArray(a);
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (this.contains(element)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return this.map.keySet().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Set)) {
            return false;
        }
        Set that = (Set)obj;
        return this.containsAll(that);
    }

    public String toString() {
        return this.map.values().toString();
    }

    private static Object canonicalize(Object value) {
        if (value instanceof Comparable) {
            return Comparables.canonicalizeForHashLookup((Comparable)value);
        }
        return value;
    }
}

