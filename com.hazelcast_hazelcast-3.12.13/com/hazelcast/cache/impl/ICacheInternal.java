/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache$Entry
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.HazelcastCacheManager;
import com.hazelcast.cache.ICache;
import java.util.Iterator;
import javax.cache.Cache;
import javax.cache.configuration.CacheEntryListenerConfiguration;

public interface ICacheInternal<K, V>
extends ICache<K, V> {
    public void open();

    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> var1, boolean var2) throws IllegalArgumentException;

    public Iterator<Cache.Entry<K, V>> iterator(int var1, int var2, boolean var3);

    public void setCacheManager(HazelcastCacheManager var1);

    public void resetCacheManager();
}

