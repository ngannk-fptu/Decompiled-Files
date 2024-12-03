/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CacheSimpleEntryListenerConfig;
import com.hazelcast.config.CacheSimpleEntryListenerConfigReadOnly;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.HotRestartConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.WanReplicationRef;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CacheSimpleConfigReadOnly
extends CacheSimpleConfig {
    public CacheSimpleConfigReadOnly(CacheSimpleConfig cacheSimpleConfig) {
        super(cacheSimpleConfig);
    }

    @Override
    public EvictionConfig getEvictionConfig() {
        EvictionConfig evictionConfig = super.getEvictionConfig();
        if (evictionConfig == null) {
            return null;
        }
        return evictionConfig.getAsReadOnly();
    }

    @Override
    public List<CacheSimpleEntryListenerConfig> getCacheEntryListeners() {
        List<CacheSimpleEntryListenerConfig> listenerConfigs = super.getCacheEntryListeners();
        ArrayList<CacheSimpleEntryListenerConfigReadOnly> readOnlyListenerConfigs = new ArrayList<CacheSimpleEntryListenerConfigReadOnly>(listenerConfigs.size());
        for (CacheSimpleEntryListenerConfig listenerConfig : listenerConfigs) {
            readOnlyListenerConfigs.add(listenerConfig.getAsReadOnly());
        }
        return Collections.unmodifiableList(readOnlyListenerConfigs);
    }

    @Override
    public CacheSimpleConfig setAsyncBackupCount(int asyncBackupCount) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setBackupCount(int backupCount) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setCacheEntryListeners(List<CacheSimpleEntryListenerConfig> cacheEntryListeners) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setCacheLoaderFactory(String cacheLoaderFactory) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setCacheWriterFactory(String cacheWriterFactory) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setEvictionConfig(EvictionConfig evictionConfig) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setExpiryPolicyFactoryConfig(CacheSimpleConfig.ExpiryPolicyFactoryConfig expiryPolicyFactoryConfig) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setExpiryPolicyFactory(String className) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setKeyType(String keyType) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setManagementEnabled(boolean managementEnabled) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setName(String name) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setReadThrough(boolean readThrough) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setStatisticsEnabled(boolean statisticsEnabled) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setValueType(String valueType) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setWriteThrough(boolean writeThrough) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig addEntryListenerConfig(CacheSimpleEntryListenerConfig listenerConfig) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public void setWanReplicationRef(WanReplicationRef wanReplicationRef) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setQuorumName(String quorumName) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public void setMergePolicy(String mergePolicy) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig addCachePartitionLostListenerConfig(CachePartitionLostListenerConfig listenerConfig) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setPartitionLostListenerConfigs(List<CachePartitionLostListenerConfig> partitionLostListenerConfigs) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public void setDisablePerEntryInvalidationEvents(boolean disablePerEntryInvalidationEvents) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }

    @Override
    public CacheSimpleConfig setHotRestartConfig(HotRestartConfig hotRestartConfig) {
        throw new UnsupportedOperationException("This config is read-only cache: " + this.getName());
    }
}

