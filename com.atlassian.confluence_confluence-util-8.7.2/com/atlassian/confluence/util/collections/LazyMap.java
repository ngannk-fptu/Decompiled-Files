/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  net.jcip.annotations.NotThreadSafe
 */
package com.atlassian.confluence.util.collections;

import com.atlassian.confluence.util.collections.LazyMapEntry;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class LazyMap<K, V>
extends AbstractMap<K, V>
implements Map<K, V> {
    private final Map<K, ? extends java.util.function.Supplier<? extends V>> delegate;
    private final Map<K, V> cache = new HashMap();

    @Deprecated
    public static <K, V> LazyMap<K, V> newInstance(Map<K, ? extends Supplier<? extends V>> delegate) {
        return LazyMap.fromSuppliersMap(Maps.transformValues(delegate, s -> () -> s.get()));
    }

    public static <K, V> LazyMap<K, V> fromSuppliersMap(Map<K, ? extends java.util.function.Supplier<? extends V>> delegate) {
        return new LazyMap<K, V>(delegate);
    }

    LazyMap(Map<K, ? extends java.util.function.Supplier<? extends V>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public V get(Object key) {
        V value = this.cache.get(key);
        if (value == null) {
            java.util.function.Supplier<V> supplier = this.delegate.get(key);
            if (supplier == null) {
                return null;
            }
            value = supplier.get();
            Object k = key;
            this.cache.put(k, value);
        }
        return value;
    }

    @Override
    public Set<K> keySet() {
        return this.delegate.keySet();
    }

    @Override
    public boolean containsKey(Object key) {
        Object k = key;
        return this.keySet().contains(k);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        HashSet result = new HashSet();
        for (K key : this.keySet()) {
            result.add(new LazyMapEntry(this, key));
        }
        return ImmutableSet.copyOf(result);
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
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }
}

