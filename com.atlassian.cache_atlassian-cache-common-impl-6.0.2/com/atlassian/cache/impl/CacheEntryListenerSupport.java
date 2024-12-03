/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheEntryListener
 */
package com.atlassian.cache.impl;

import com.atlassian.cache.CacheEntryListener;

public interface CacheEntryListenerSupport<K, V> {
    public static final CacheEntryListenerSupport EMPTY = new CacheEntryListenerSupport(){

        public void add(CacheEntryListener listener, boolean includeValues) {
        }

        public void remove(CacheEntryListener listener) {
        }

        public void notifyAdd(Object key, Object value) {
        }

        public void notifyEvict(Object key, Object oldValue) {
        }

        public void notifyRemove(Object key, Object oldValue) {
        }

        public void notifyUpdate(Object key, Object value, Object oldValue) {
        }
    };

    public void add(CacheEntryListener<K, V> var1, boolean var2);

    public void remove(CacheEntryListener<K, V> var1);

    public void notifyAdd(K var1, V var2);

    public void notifyEvict(K var1, V var2);

    public void notifyRemove(K var1, V var2);

    public void notifyUpdate(K var1, V var2, V var3);
}

