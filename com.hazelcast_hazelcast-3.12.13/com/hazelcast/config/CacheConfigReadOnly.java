/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 *  javax.cache.configuration.Factory
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.integration.CacheLoader
 *  javax.cache.integration.CacheWriter
 */
package com.hazelcast.config;

import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.CacheConfiguration;
import com.hazelcast.config.CacheEvictionConfig;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.HotRestartConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;

@BinaryInterface
public class CacheConfigReadOnly<K, V>
extends CacheConfig<K, V> {
    CacheConfigReadOnly(CacheConfig config) {
        super(config);
    }

    @Override
    public CacheEvictionConfig getEvictionConfig() {
        CacheEvictionConfig evictionConfig = super.getEvictionConfig();
        if (evictionConfig == null) {
            return null;
        }
        return evictionConfig.getAsReadOnly();
    }

    @Override
    public WanReplicationRef getWanReplicationRef() {
        WanReplicationRef wanReplicationRef = super.getWanReplicationRef();
        if (wanReplicationRef == null) {
            return null;
        }
        return wanReplicationRef.getAsReadOnly();
    }

    @Override
    public String getQuorumName() {
        return super.getQuorumName();
    }

    @Override
    public Iterable<CacheEntryListenerConfiguration<K, V>> getCacheEntryListenerConfigurations() {
        Iterable listenerConfigurations = super.getCacheEntryListenerConfigurations();
        return Collections.unmodifiableSet((Set)listenerConfigurations);
    }

    @Override
    public CacheConfig<K, V> addCacheEntryListenerConfiguration(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> removeCacheEntryListenerConfiguration(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setName(String name) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setManagerPrefix(String managerPrefix) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setUriString(String uriString) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setBackupCount(int backupCount) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setAsyncBackupCount(int asyncBackupCount) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setEvictionConfig(EvictionConfig evictionConfig) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setManagementEnabled(boolean enabled) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfiguration<K, V> setStatisticsEnabled(boolean enabled) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setTypes(Class<K> keyType, Class<V> valueType) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setStoreByValue(boolean storeByValue) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setWanReplicationRef(WanReplicationRef wanReplicationRef) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setQuorumName(String quorumName) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfiguration<K, V> setHotRestartConfig(HotRestartConfig hotRestartConfig) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfig<K, V> setPartitionLostListenerConfigs(List<CachePartitionLostListenerConfig> partitionLostListenerConfigs) {
        throw this.throwReadOnly();
    }

    @Override
    public void setMergePolicy(String mergePolicy) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfiguration<K, V> setExpiryPolicyFactory(Factory<? extends ExpiryPolicy> expiryPolicyFactory) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfiguration<K, V> setCacheLoaderFactory(Factory<? extends CacheLoader<K, V>> cacheLoaderFactory) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfiguration<K, V> setCacheWriterFactory(Factory<? extends CacheWriter<? super K, ? super V>> cacheWriterFactory) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfiguration<K, V> setWriteThrough(boolean isWriteThrough) {
        throw this.throwReadOnly();
    }

    @Override
    public CacheConfiguration<K, V> setReadThrough(boolean isReadThrough) {
        throw this.throwReadOnly();
    }

    @Override
    public void setDisablePerEntryInvalidationEvents(boolean disablePerEntryInvalidationEvents) {
        throw this.throwReadOnly();
    }

    private UnsupportedOperationException throwReadOnly() {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

