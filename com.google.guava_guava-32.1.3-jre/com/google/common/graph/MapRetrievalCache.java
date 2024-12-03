/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.common.graph.MapIteratorCache;
import java.util.Map;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
final class MapRetrievalCache<K, V>
extends MapIteratorCache<K, V> {
    @CheckForNull
    private volatile transient CacheEntry<K, V> cacheEntry1;
    @CheckForNull
    private volatile transient CacheEntry<K, V> cacheEntry2;

    MapRetrievalCache(Map<K, V> backingMap) {
        super(backingMap);
    }

    @Override
    @CheckForNull
    V get(Object key) {
        Preconditions.checkNotNull(key);
        V value = this.getIfCached(key);
        if (value != null) {
            return value;
        }
        value = this.getWithoutCaching(key);
        if (value != null) {
            this.addToCache(key, value);
        }
        return value;
    }

    @Override
    @CheckForNull
    V getIfCached(@CheckForNull Object key) {
        Object value = super.getIfCached(key);
        if (value != null) {
            return value;
        }
        CacheEntry<K, V> entry = this.cacheEntry1;
        if (entry != null && entry.key == key) {
            return entry.value;
        }
        entry = this.cacheEntry2;
        if (entry != null && entry.key == key) {
            this.addToCache(entry);
            return entry.value;
        }
        return null;
    }

    @Override
    void clearCache() {
        super.clearCache();
        this.cacheEntry1 = null;
        this.cacheEntry2 = null;
    }

    private void addToCache(K key, V value) {
        this.addToCache(new CacheEntry<K, V>(key, value));
    }

    private void addToCache(CacheEntry<K, V> entry) {
        this.cacheEntry2 = this.cacheEntry1;
        this.cacheEntry1 = entry;
    }

    private static final class CacheEntry<K, V> {
        final K key;
        final V value;

        CacheEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}

