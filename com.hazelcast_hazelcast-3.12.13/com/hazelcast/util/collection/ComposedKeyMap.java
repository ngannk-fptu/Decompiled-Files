/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.collection;

import com.hazelcast.util.Preconditions;
import java.util.HashMap;
import java.util.Map;

public class ComposedKeyMap<K1, K2, V> {
    private final Map<K1, Map<K2, V>> backingMap = new HashMap<K1, Map<K2, V>>();

    public V put(K1 key1, K2 key2, V value) {
        Preconditions.checkNotNull(key1, "Key1 cannot be null");
        Preconditions.checkNotNull(key2, "Key2 cannot be null");
        Preconditions.checkNotNull(value, "Value cannot be null");
        Map<K2, V> innerMap = this.backingMap.get(key1);
        if (innerMap == null) {
            innerMap = new HashMap<K2, V>();
            this.backingMap.put(key1, innerMap);
        }
        return innerMap.put(key2, value);
    }

    public V get(K1 key1, K2 key2) {
        Preconditions.checkNotNull(key1, "Key1 cannot be null");
        Preconditions.checkNotNull(key2, "Key2 cannot be null");
        Map<K2, V> innerMap = this.backingMap.get(key1);
        if (innerMap == null) {
            return null;
        }
        return innerMap.get(key2);
    }
}

