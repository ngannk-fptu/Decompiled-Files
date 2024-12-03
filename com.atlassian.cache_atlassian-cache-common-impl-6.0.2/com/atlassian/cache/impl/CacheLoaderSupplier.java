/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.Supplier
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.Supplier;

public class CacheLoaderSupplier<K, V>
implements Supplier<V> {
    private final K key;
    private final CacheLoader<? super K, ? extends V> loader;

    public CacheLoaderSupplier(K key, CacheLoader<? super K, ? extends V> loader) {
        this.key = key;
        this.loader = loader;
    }

    public V get() {
        return (V)this.loader.load(this.key);
    }
}

