/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CachedReferenceListener
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.CachedReferenceListener;

public interface CachedReferenceListenerSupport<V> {
    public static final CachedReferenceListenerSupport EMPTY = new CachedReferenceListenerSupport(){

        public void add(CachedReferenceListener listener, boolean includeValues) {
        }

        public void remove(CachedReferenceListener listener) {
        }

        public void notifyEvict(Object oldValue) {
        }

        public void notifySet(Object value) {
        }

        public void notifyReset(Object oldValue) {
        }
    };

    public void add(CachedReferenceListener<V> var1, boolean var2);

    public void remove(CachedReferenceListener<V> var1);

    public void notifyEvict(V var1);

    public void notifySet(V var1);

    public void notifyReset(V var1);
}

