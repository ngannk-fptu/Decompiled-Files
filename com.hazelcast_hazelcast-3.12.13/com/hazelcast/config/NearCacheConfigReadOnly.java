/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NearCachePreloaderConfig;

public class NearCacheConfigReadOnly
extends NearCacheConfig {
    public NearCacheConfigReadOnly() {
    }

    public NearCacheConfigReadOnly(NearCacheConfig config) {
        super(config);
    }

    @Override
    public NearCacheConfig setName(String name) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCacheConfig setTimeToLiveSeconds(int timeToLiveSeconds) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCacheConfig setMaxSize(int maxSize) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCacheConfig setEvictionPolicy(String evictionPolicy) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCacheConfig setMaxIdleSeconds(int maxIdleSeconds) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCacheConfig setSerializeKeys(boolean serializeKeys) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCacheConfig setInvalidateOnChange(boolean invalidateOnChange) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCacheConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCacheConfig setInMemoryFormat(String inMemoryFormat) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCacheConfig setCacheLocalEntries(boolean cacheLocalEntries) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCacheConfig setLocalUpdatePolicy(NearCacheConfig.LocalUpdatePolicy localUpdatePolicy) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCacheConfig setEvictionConfig(EvictionConfig evictionConfig) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public EvictionConfig getEvictionConfig() {
        return super.getEvictionConfig().getAsReadOnly();
    }

    @Override
    public NearCacheConfig setPreloaderConfig(NearCachePreloaderConfig preloaderConfig) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public NearCachePreloaderConfig getPreloaderConfig() {
        return super.getPreloaderConfig().getAsReadOnly();
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("NearCacheConfigReadOnly is not serializable");
    }
}

