/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CacheConfig;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.tenantcontrol.TenantControl;

@PrivateApi
public final class CacheConfigAccessor {
    private CacheConfigAccessor() {
    }

    public static <K, V> TenantControl getTenantControl(CacheConfig<K, V> cacheConfig) {
        return cacheConfig.getTenantControl();
    }

    public static <K, V> void setTenantControl(CacheConfig<K, V> cacheConfig, TenantControl tenantControl) {
        cacheConfig.setTenantControl(tenantControl);
    }
}

