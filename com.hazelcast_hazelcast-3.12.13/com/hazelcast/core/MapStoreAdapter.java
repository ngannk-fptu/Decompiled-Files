/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.MapStore;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.Map;

public class MapStoreAdapter<K, V>
implements MapStore<K, V> {
    @Override
    public void delete(K key) {
    }

    @Override
    public void store(K key, V value) {
    }

    @Override
    public void storeAll(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            this.store(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void deleteAll(Collection<K> keys) {
        for (K key : keys) {
            this.delete(key);
        }
    }

    @Override
    public V load(K key) {
        return null;
    }

    @Override
    public Map<K, V> loadAll(Collection<K> keys) {
        Map<K, V> result = MapUtil.createHashMap(keys.size());
        for (K key : keys) {
            V value = this.load(key);
            if (value == null) continue;
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Iterable<K> loadAllKeys() {
        return null;
    }
}

