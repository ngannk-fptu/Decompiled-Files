/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ShrinkableMap<K, V>
implements Map<K, V> {
    private final Map<K, V> m_delegate;

    public ShrinkableMap(Map<K, V> delegate) {
        this.m_delegate = delegate;
    }

    @Override
    public int size() {
        return this.m_delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.m_delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return this.m_delegate.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return this.m_delegate.containsValue(o);
    }

    @Override
    public V get(Object o) {
        return this.m_delegate.get(o);
    }

    @Override
    public V put(K k, V v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public V remove(Object o) {
        return this.m_delegate.remove(o);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        this.m_delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.m_delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.m_delegate.values();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.m_delegate.entrySet();
    }
}

