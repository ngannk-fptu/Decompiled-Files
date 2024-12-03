/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 */
package com.atlassian.user.util;

import com.atlassian.cache.Cache;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GenericCacheWrapper<K, V> {
    private final Cache cache;

    public GenericCacheWrapper(Cache cache) {
        this.cache = cache;
    }

    public String getName() {
        return this.cache.getName();
    }

    public Collection<K> getKeys() {
        return this.cache.getKeys();
    }

    public V get(K key) {
        return (V)this.cache.get(key);
    }

    public void put(K key, V value) {
        this.cache.put(key, value);
    }

    public void remove(K key) {
        this.cache.remove(key);
    }

    public void removeAll() {
        this.cache.removeAll();
    }
}

