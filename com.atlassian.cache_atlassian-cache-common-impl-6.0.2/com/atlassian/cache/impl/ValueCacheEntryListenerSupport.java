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
import com.atlassian.cache.impl.LazyCacheEntryListenerSupport;
import io.atlassian.util.concurrent.LazyReference;

public class ValueCacheEntryListenerSupport<K, V>
implements CacheEntryListenerSupport<K, V> {
    private final LazyReference<Void> initReference = new LazyReference<Void>(){

        protected Void create() throws Exception {
            ValueCacheEntryListenerSupport.this.init(ValueCacheEntryListenerSupport.this);
            return null;
        }
    };
    private final CacheEntryListenerSupport<K, V> valueListenerSupport = new LazyCacheEntryListenerSupport<K, V>(){

        @Override
        protected void init() {
            ValueCacheEntryListenerSupport.this.initReference.get();
            ValueCacheEntryListenerSupport.this.initValue(this);
        }
    };
    private final CacheEntryListenerSupport<K, V> valuelessListenerSupport = new LazyCacheEntryListenerSupport<K, V>(){

        @Override
        protected void init() {
            ValueCacheEntryListenerSupport.this.initReference.get();
            ValueCacheEntryListenerSupport.this.initValueless(this);
        }
    };

    protected void init(CacheEntryListenerSupport<K, V> actualListenerSupport) {
    }

    protected void initValue(CacheEntryListenerSupport<K, V> actualListenerSupport) {
    }

    protected void initValueless(CacheEntryListenerSupport<K, V> actualListenerSupport) {
    }

    @Override
    public void add(CacheEntryListener<K, V> listener, boolean includeValues) {
        if (includeValues) {
            this.valueListenerSupport.add(listener, true);
        } else {
            this.valuelessListenerSupport.add(listener, false);
        }
    }

    @Override
    public void remove(CacheEntryListener<K, V> listener) {
        this.valueListenerSupport.remove(listener);
        this.valuelessListenerSupport.remove(listener);
    }

    @Override
    public void notifyAdd(K key, V value) {
        this.valueListenerSupport.notifyAdd(key, value);
        this.valuelessListenerSupport.notifyAdd(key, null);
    }

    @Override
    public void notifyEvict(K key, V oldValue) {
        this.valueListenerSupport.notifyEvict(key, oldValue);
        this.valuelessListenerSupport.notifyEvict(key, null);
    }

    @Override
    public void notifyRemove(K key, V oldValue) {
        this.valueListenerSupport.notifyRemove(key, oldValue);
        this.valuelessListenerSupport.notifyRemove(key, null);
    }

    @Override
    public void notifyUpdate(K key, V value, V oldValue) {
        this.valueListenerSupport.notifyUpdate(key, value, oldValue);
        this.valuelessListenerSupport.notifyUpdate(key, null, null);
    }
}

