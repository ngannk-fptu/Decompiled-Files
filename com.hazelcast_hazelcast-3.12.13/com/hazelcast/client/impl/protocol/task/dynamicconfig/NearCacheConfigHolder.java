/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.task.dynamicconfig.EvictionConfigHolder;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NearCachePreloaderConfig;
import com.hazelcast.spi.serialization.SerializationService;

public class NearCacheConfigHolder {
    private final String name;
    private final String inMemoryFormat;
    private final boolean serializeKeys;
    private final boolean invalidateOnChange;
    private final int timeToLiveSeconds;
    private final int maxIdleSeconds;
    private final EvictionConfigHolder evictionConfigHolder;
    private final boolean cacheLocalEntries;
    private final String localUpdatePolicy;
    private final NearCachePreloaderConfig preloaderConfig;

    public NearCacheConfigHolder(String name, String inMemoryFormat, boolean serializeKeys, boolean invalidateOnChange, int timeToLiveSeconds, int maxIdleSeconds, EvictionConfigHolder evictionConfigHolder, boolean cacheLocalEntries, String localUpdatePolicy, NearCachePreloaderConfig preloaderConfig) {
        this.name = name;
        this.inMemoryFormat = inMemoryFormat;
        this.serializeKeys = serializeKeys;
        this.invalidateOnChange = invalidateOnChange;
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.maxIdleSeconds = maxIdleSeconds;
        this.evictionConfigHolder = evictionConfigHolder;
        this.cacheLocalEntries = cacheLocalEntries;
        this.localUpdatePolicy = localUpdatePolicy;
        this.preloaderConfig = preloaderConfig;
    }

    public String getName() {
        return this.name;
    }

    public String getInMemoryFormat() {
        return this.inMemoryFormat;
    }

    public boolean isSerializeKeys() {
        return this.serializeKeys;
    }

    public boolean isInvalidateOnChange() {
        return this.invalidateOnChange;
    }

    public int getTimeToLiveSeconds() {
        return this.timeToLiveSeconds;
    }

    public int getMaxIdleSeconds() {
        return this.maxIdleSeconds;
    }

    public EvictionConfigHolder getEvictionConfigHolder() {
        return this.evictionConfigHolder;
    }

    public boolean isCacheLocalEntries() {
        return this.cacheLocalEntries;
    }

    public String getLocalUpdatePolicy() {
        return this.localUpdatePolicy;
    }

    public NearCachePreloaderConfig getPreloaderConfig() {
        return this.preloaderConfig;
    }

    public NearCacheConfig asNearCacheConfig(SerializationService serializationService) {
        NearCacheConfig config = new NearCacheConfig();
        config.setName(this.name);
        config.setInMemoryFormat(this.inMemoryFormat);
        config.setSerializeKeys(this.serializeKeys);
        config.setInvalidateOnChange(this.invalidateOnChange);
        config.setTimeToLiveSeconds(this.timeToLiveSeconds);
        config.setMaxIdleSeconds(this.maxIdleSeconds);
        config.setEvictionConfig(this.evictionConfigHolder.asEvictionConfg(serializationService));
        config.setCacheLocalEntries(this.cacheLocalEntries);
        config.setLocalUpdatePolicy(NearCacheConfig.LocalUpdatePolicy.valueOf(this.localUpdatePolicy));
        config.setPreloaderConfig(this.preloaderConfig);
        return config;
    }

    public static NearCacheConfigHolder of(NearCacheConfig config, SerializationService serializationService) {
        if (config == null) {
            return null;
        }
        return new NearCacheConfigHolder(config.getName(), config.getInMemoryFormat().name(), config.isSerializeKeys(), config.isInvalidateOnChange(), config.getTimeToLiveSeconds(), config.getMaxIdleSeconds(), EvictionConfigHolder.of(config.getEvictionConfig(), serializationService), config.isCacheLocalEntries(), config.getLocalUpdatePolicy().name(), config.getPreloaderConfig());
    }
}

