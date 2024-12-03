/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.Cache;
import groovy.json.internal.CacheType;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleCache<K, V>
implements Cache<K, V> {
    Map<K, V> map = new LinkedHashMap();

    public SimpleCache(int limit, CacheType type) {
        this.map = type.equals((Object)CacheType.LRU) ? new InternalCacheLinkedList(limit, true) : new InternalCacheLinkedList(limit, false);
    }

    public SimpleCache(int limit) {
        this.map = new InternalCacheLinkedList(limit, true);
    }

    @Override
    public void put(K key, V value) {
        this.map.put(key, value);
    }

    @Override
    public V get(K key) {
        return this.map.get(key);
    }

    @Override
    public V getSilent(K key) {
        V value = this.map.get(key);
        if (value != null) {
            this.map.remove(key);
            this.map.put(key, value);
        }
        return value;
    }

    @Override
    public void remove(K key) {
        this.map.remove(key);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    public String toString() {
        return this.map.toString();
    }

    private static class InternalCacheLinkedList<K, V>
    extends LinkedHashMap<K, V> {
        final int limit;

        InternalCacheLinkedList(int limit, boolean lru) {
            super(16, 0.75f, lru);
            this.limit = limit;
        }

        @Override
        protected final boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return super.size() > this.limit;
        }
    }
}

