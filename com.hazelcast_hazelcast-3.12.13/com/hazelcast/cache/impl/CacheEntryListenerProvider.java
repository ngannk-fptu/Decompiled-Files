/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.event.CacheEntryListener
 */
package com.hazelcast.cache.impl;

import javax.cache.event.CacheEntryListener;

public interface CacheEntryListenerProvider<K, V> {
    public CacheEntryListener<K, V> getCacheEntryListener();
}

