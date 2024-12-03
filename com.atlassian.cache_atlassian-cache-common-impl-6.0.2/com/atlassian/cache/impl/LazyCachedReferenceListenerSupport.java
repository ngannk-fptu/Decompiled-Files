/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CachedReferenceListener
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.CachedReferenceListener;
import com.atlassian.cache.impl.CachedReferenceListenerSupport;
import com.atlassian.cache.impl.DefaultCachedReferenceListenerSupport;
import io.atlassian.util.concurrent.LazyReference;

public class LazyCachedReferenceListenerSupport<V>
implements CachedReferenceListenerSupport<V> {
    private final LazyReference<CachedReferenceListenerSupport<V>> lazyListenerSupport = new LazyReference<CachedReferenceListenerSupport<V>>(){

        protected CachedReferenceListenerSupport<V> create() throws Exception {
            LazyCachedReferenceListenerSupport.this.init();
            return LazyCachedReferenceListenerSupport.this.createDelegate();
        }
    };

    protected void init() {
    }

    protected CachedReferenceListenerSupport<V> createDelegate() {
        return new DefaultCachedReferenceListenerSupport();
    }

    private CachedReferenceListenerSupport<V> loadDelegate() {
        return (CachedReferenceListenerSupport)this.lazyListenerSupport.get();
    }

    private CachedReferenceListenerSupport<V> getDelegate() {
        if (this.lazyListenerSupport.isInitialized()) {
            return (CachedReferenceListenerSupport)this.lazyListenerSupport.get();
        }
        return CachedReferenceListenerSupport.EMPTY;
    }

    @Override
    public void add(CachedReferenceListener<V> listener, boolean includeValues) {
        this.loadDelegate().add(listener, includeValues);
    }

    @Override
    public void remove(CachedReferenceListener<V> listener) {
        this.getDelegate().remove(listener);
    }

    @Override
    public void notifyEvict(V oldValue) {
        this.getDelegate().notifyEvict(oldValue);
    }

    @Override
    public void notifySet(V value) {
        this.getDelegate().notifySet(value);
    }

    @Override
    public void notifyReset(V oldValue) {
        this.getDelegate().notifyReset(oldValue);
    }
}

