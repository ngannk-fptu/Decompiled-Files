/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.management.CacheMXBean
 */
package com.hazelcast.cache.impl;

import com.hazelcast.config.CacheConfig;
import javax.cache.management.CacheMXBean;

public class CacheMXBeanImpl
implements CacheMXBean {
    private CacheConfig cacheConfig;

    public CacheMXBeanImpl(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    public String getKeyType() {
        return this.cacheConfig.getKeyType().getName();
    }

    public String getValueType() {
        return this.cacheConfig.getValueType().getName();
    }

    public boolean isReadThrough() {
        return this.cacheConfig.isReadThrough();
    }

    public boolean isWriteThrough() {
        return this.cacheConfig.isWriteThrough();
    }

    public boolean isStoreByValue() {
        return this.cacheConfig.isStoreByValue();
    }

    public boolean isStatisticsEnabled() {
        return this.cacheConfig.isStatisticsEnabled();
    }

    public boolean isManagementEnabled() {
        return this.cacheConfig.isManagementEnabled();
    }
}

