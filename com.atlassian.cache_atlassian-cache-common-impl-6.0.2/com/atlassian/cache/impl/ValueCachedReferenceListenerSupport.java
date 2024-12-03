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
import com.atlassian.cache.impl.LazyCachedReferenceListenerSupport;
import io.atlassian.util.concurrent.LazyReference;

public class ValueCachedReferenceListenerSupport<V>
implements CachedReferenceListenerSupport<V> {
    private final LazyReference<Void> initReference = new LazyReference<Void>(){

        protected Void create() throws Exception {
            ValueCachedReferenceListenerSupport.this.init(ValueCachedReferenceListenerSupport.this);
            return null;
        }
    };
    private final CachedReferenceListenerSupport<V> valueListenerSupport = new LazyCachedReferenceListenerSupport<V>(){

        @Override
        protected void init() {
            ValueCachedReferenceListenerSupport.this.initReference.get();
            ValueCachedReferenceListenerSupport.this.initValue(this);
        }
    };
    private final CachedReferenceListenerSupport<V> valuelessListenerSupport = new LazyCachedReferenceListenerSupport<V>(){

        @Override
        protected void init() {
            ValueCachedReferenceListenerSupport.this.initReference.get();
            ValueCachedReferenceListenerSupport.this.initValueless(this);
        }
    };

    protected void init(CachedReferenceListenerSupport<V> actualListenerSupport) {
    }

    protected void initValue(CachedReferenceListenerSupport<V> actualListenerSupport) {
    }

    protected void initValueless(CachedReferenceListenerSupport<V> actualListenerSupport) {
    }

    @Override
    public void add(CachedReferenceListener<V> listener, boolean includeValues) {
        if (includeValues) {
            this.valueListenerSupport.add(listener, true);
        } else {
            this.valuelessListenerSupport.add(listener, false);
        }
    }

    @Override
    public void remove(CachedReferenceListener<V> listener) {
        this.valueListenerSupport.remove(listener);
        this.valuelessListenerSupport.remove(listener);
    }

    @Override
    public void notifyEvict(V oldValue) {
        this.valueListenerSupport.notifyEvict(oldValue);
        this.valuelessListenerSupport.notifyEvict(null);
    }

    @Override
    public void notifySet(V value) {
        this.valueListenerSupport.notifySet(value);
        this.valuelessListenerSupport.notifySet(null);
    }

    @Override
    public void notifyReset(V oldValue) {
        this.valueListenerSupport.notifyReset(oldValue);
        this.valuelessListenerSupport.notifyReset(null);
    }
}

