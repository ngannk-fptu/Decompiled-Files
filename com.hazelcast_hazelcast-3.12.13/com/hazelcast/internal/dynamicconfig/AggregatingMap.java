/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.dynamicconfig;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AggregatingMap<K, V>
implements Map<K, V> {
    private final Map<K, V> map1;
    private final Map<K, V> map2;

    private AggregatingMap(Map<K, V> map1, Map<K, V> map2) {
        if (map1 == null) {
            map1 = Collections.emptyMap();
        }
        if (map2 == null) {
            map2 = Collections.emptyMap();
        }
        this.map1 = map1;
        this.map2 = map2;
    }

    public static <K, V> Map<K, V> aggregate(Map<K, V> map1, Map<K, V> map2) {
        return new AggregatingMap<K, V>(map1, map2);
    }

    @Override
    public int size() {
        return this.map1.size() + this.map2.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map1.isEmpty() && this.map2.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map1.containsKey(key) || this.map2.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map1.containsValue(value) || this.map2.containsValue(value);
    }

    @Override
    public V get(Object key) {
        V v = this.map1.get(key);
        return v == null ? this.map2.get(key) : v;
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("aggregating map is read only");
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("aggregating map is read only");
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("aggregating map is read only");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("aggregating map is read only");
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> keys = new HashSet<K>(this.map1.keySet());
        keys.addAll(this.map2.keySet());
        return Collections.unmodifiableSet(keys);
    }

    @Override
    public Collection<V> values() {
        ArrayList<V> values = new ArrayList<V>(this.map1.values());
        values.addAll(this.map2.values());
        return Collections.unmodifiableCollection(values);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet1 = this.map1.entrySet();
        Set<Map.Entry<K, V>> entrySet2 = this.map2.entrySet();
        HashSet<Map.Entry<K, V>> aggregatedEntrySet = new HashSet<Map.Entry<K, V>>();
        this.copyEntries(entrySet1, aggregatedEntrySet);
        this.copyEntries(entrySet2, aggregatedEntrySet);
        return Collections.unmodifiableSet(aggregatedEntrySet);
    }

    private void copyEntries(Set<Map.Entry<K, V>> source, Set<Map.Entry<K, V>> destination) {
        for (Map.Entry<K, V> entry : source) {
            K key = entry.getKey();
            V value = entry.getValue();
            destination.add(new AbstractMap.SimpleEntry<K, V>(key, value));
        }
    }
}

