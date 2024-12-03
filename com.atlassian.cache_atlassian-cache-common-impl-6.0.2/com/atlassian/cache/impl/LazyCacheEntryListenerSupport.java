/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheEntryListener
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.impl.CacheEntryListenerSupport;
import com.atlassian.cache.impl.DefaultCacheEntryListenerSupport;
import io.atlassian.util.concurrent.LazyReference;

public class LazyCacheEntryListenerSupport<K, V>
implements CacheEntryListenerSupport<K, V> {
    private final LazyReference<CacheEntryListenerSupport<K, V>> lazyListenerSupport = new LazyReference<CacheEntryListenerSupport<K, V>>(){

        protected CacheEntryListenerSupport<K, V> create() throws Exception {
            LazyCacheEntryListenerSupport.this.init();
            return LazyCacheEntryListenerSupport.this.createDelegate();
        }
    };

    protected void init() {
    }

    protected CacheEntryListenerSupport<K, V> createDelegate() {
        return new DefaultCacheEntryListenerSupport();
    }

    private CacheEntryListenerSupport<K, V> loadDelegate() {
        return (CacheEntryListenerSupport)this.lazyListenerSupport.get();
    }

    private CacheEntryListenerSupport<K, V> getDelegate() {
        if (this.lazyListenerSupport.isInitialized()) {
            return (CacheEntryListenerSupport)this.lazyListenerSupport.get();
        }
        return CacheEntryListenerSupport.EMPTY;
    }

    @Override
    public void add(CacheEntryListener<K, V> listener, boolean includeValues) {
        this.loadDelegate().add(listener, includeValues);
    }

    @Override
    public void remove(CacheEntryListener<K, V> listener) {
        this.getDelegate().remove(listener);
    }

    @Override
    public void notifyAdd(K key, V value) {
        this.getDelegate().notifyAdd(key, value);
    }

    @Override
    public void notifyEvict(K key, V oldValue) {
        this.getDelegate().notifyEvict(key, oldValue);
    }

    @Override
    public void notifyRemove(K key, V oldValue) {
        this.getDelegate().notifyRemove(key, oldValue);
    }

    @Override
    public void notifyUpdate(K key, V value, V oldValue) {
        this.getDelegate().notifyUpdate(key, value, oldValue);
    }
}

