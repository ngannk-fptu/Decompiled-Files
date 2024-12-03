/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 *  javax.cache.configuration.CompleteConfiguration
 *  javax.cache.configuration.Factory
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.integration.CacheLoader
 *  javax.cache.integration.CacheWriter
 */
package com.hazelcast.config;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;

public interface CacheConfiguration<K, V>
extends CompleteConfiguration<K, V> {
    public CacheConfiguration<K, V> setTypes(Class<K> var1, Class<V> var2);

    public CacheConfiguration<K, V> addCacheEntryListenerConfiguration(CacheEntryListenerConfiguration<K, V> var1);

    public CacheConfiguration<K, V> removeCacheEntryListenerConfiguration(CacheEntryListenerConfiguration<K, V> var1);

    public CacheConfiguration<K, V> setCacheLoaderFactory(Factory<? extends CacheLoader<K, V>> var1);

    public CacheConfiguration<K, V> setCacheWriterFactory(Factory<? extends CacheWriter<? super K, ? super V>> var1);

    public CacheConfiguration<K, V> setExpiryPolicyFactory(Factory<? extends ExpiryPolicy> var1);

    public CacheConfiguration<K, V> setReadThrough(boolean var1);

    public CacheConfiguration<K, V> setWriteThrough(boolean var1);

    public CacheConfiguration<K, V> setStoreByValue(boolean var1);

    public CacheConfiguration<K, V> setStatisticsEnabled(boolean var1);

    public CacheConfiguration<K, V> setManagementEnabled(boolean var1);
}

