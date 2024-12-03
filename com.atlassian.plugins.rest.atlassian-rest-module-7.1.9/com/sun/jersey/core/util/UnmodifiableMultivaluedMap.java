/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;

public class UnmodifiableMultivaluedMap<K, V>
implements MultivaluedMap<K, V> {
    private final MultivaluedMap<K, V> delegate;

    public UnmodifiableMultivaluedMap(MultivaluedMap<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void putSingle(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V getFirst(K key) {
        return this.delegate.getFirst(key);
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
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override
    public List<V> get(Object key) {
        return (List)this.delegate.get(key);
    }

    @Override
    public List<V> put(K key, List<V> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<V> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends List<V>> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(this.delegate.keySet());
    }

    @Override
    public Collection<List<V>> values() {
        return Collections.unmodifiableCollection(this.delegate.values());
    }

    @Override
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return Collections.unmodifiableSet(this.delegate.entrySet());
    }
}

