/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public final class NearCacheConfigAccessor {
    private NearCacheConfigAccessor() {
    }

    public static NearCacheConfig copyWithInitializedDefaultMaxSizeForOnHeapMaps(NearCacheConfig nearCacheConfig) {
        if (nearCacheConfig == null) {
            return null;
        }
        EvictionConfig evictionConfig = nearCacheConfig.getEvictionConfig();
        if (nearCacheConfig.getInMemoryFormat() == InMemoryFormat.NATIVE || evictionConfig.sizeConfigured) {
            return nearCacheConfig;
        }
        EvictionConfig copyEvictionConfig = new EvictionConfig(evictionConfig).setSize(Integer.MAX_VALUE);
        return new NearCacheConfig(nearCacheConfig).setEvictionConfig(copyEvictionConfig);
    }
}

