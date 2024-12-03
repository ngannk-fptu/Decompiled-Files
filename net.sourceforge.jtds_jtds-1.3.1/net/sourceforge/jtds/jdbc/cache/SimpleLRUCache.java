/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleLRUCache<K, V> {
    private final Map<K, V> _Map;

    public SimpleLRUCache(final int limit) {
        this._Map = new LinkedHashMap<K, V>(limit + 10, 0.75f, true){

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return this.size() > limit;
            }
        };
    }

    public synchronized V put(K key, V value) {
        return this._Map.put(key, value);
    }

    public synchronized V get(K key) {
        return this._Map.get(key);
    }
}

