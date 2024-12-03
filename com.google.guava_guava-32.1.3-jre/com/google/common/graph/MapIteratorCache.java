/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 */
package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
class MapIteratorCache<K, V> {
    private final Map<K, V> backingMap;
    @CheckForNull
    private volatile transient Map.Entry<K, V> cacheEntry;

    MapIteratorCache(Map<K, V> backingMap) {
        this.backingMap = Preconditions.checkNotNull(backingMap);
    }

    @CheckForNull
    @CanIgnoreReturnValue
    final V put(K key, V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        this.clearCache();
        return this.backingMap.put(key, value);
    }

    @CheckForNull
    @CanIgnoreReturnValue
    final V remove(Object key) {
        Preconditions.checkNotNull(key);
        this.clearCache();
        return this.backingMap.remove(key);
    }

    final void clear() {
        this.clearCache();
        this.backingMap.clear();
    }

    @CheckForNull
    V get(Object key) {
        Preconditions.checkNotNull(key);
        V value = this.getIfCached(key);
        if (value == null) {
            return this.getWithoutCaching(key);
        }
        return value;
    }

    @CheckForNull
    final V getWithoutCaching(Object key) {
        Preconditions.checkNotNull(key);
        return this.backingMap.get(key);
    }

    final boolean containsKey(@CheckForNull Object key) {
        return this.getIfCached(key) != null || this.backingMap.containsKey(key);
    }

    final Set<K> unmodifiableKeySet() {
        return new AbstractSet<K>(){

            @Override
            public UnmodifiableIterator<K> iterator() {
                final Iterator entryIterator = MapIteratorCache.this.backingMap.entrySet().iterator();
                return new UnmodifiableIterator<K>(){

                    @Override
                    public boolean hasNext() {
                        return entryIterator.hasNext();
                    }

                    @Override
                    public K next() {
                        Map.Entry entry = (Map.Entry)entryIterator.next();
                        MapIteratorCache.this.cacheEntry = entry;
                        return entry.getKey();
                    }
                };
            }

            @Override
            public int size() {
                return MapIteratorCache.this.backingMap.size();
            }

            @Override
            public boolean contains(@CheckForNull Object key) {
                return MapIteratorCache.this.containsKey(key);
            }
        };
    }

    @CheckForNull
    V getIfCached(@CheckForNull Object key) {
        Map.Entry<K, V> entry = this.cacheEntry;
        if (entry != null && entry.getKey() == key) {
            return entry.getValue();
        }
        return null;
    }

    void clearCache() {
        this.cacheEntry = null;
    }
}

