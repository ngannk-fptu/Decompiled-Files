/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.collection;

import com.hazelcast.util.Preconditions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InternalSetMultimap<K, V> {
    private final Map<K, Set<V>> backingMap = new HashMap<K, Set<V>>();

    public void put(K key, V value) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        Preconditions.checkNotNull(value, "Value cannot be null");
        Set<V> values = this.backingMap.get(key);
        if (values == null) {
            values = new HashSet<V>();
            this.backingMap.put(key, values);
        }
        values.add(value);
    }

    public Set<V> get(K key) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        return this.backingMap.get(key);
    }

    public Set<Map.Entry<K, Set<V>>> entrySet() {
        return this.backingMap.entrySet();
    }
}

