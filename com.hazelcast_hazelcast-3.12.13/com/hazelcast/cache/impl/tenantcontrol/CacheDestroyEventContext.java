/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache
 */
package com.hazelcast.cache.impl.tenantcontrol;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CacheProxy;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.CacheConfigAccessor;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.tenantcontrol.DestroyEventContext;
import com.hazelcast.spi.tenantcontrol.TenantControl;
import java.io.IOException;
import javax.cache.Cache;

public class CacheDestroyEventContext
implements DestroyEventContext<Cache>,
IdentifiedDataSerializable {
    private String cacheName;

    public CacheDestroyEventContext() {
    }

    public CacheDestroyEventContext(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public void destroy(Cache context) {
        if (context instanceof CacheProxy) {
            CacheProxy cache = (CacheProxy)context;
            CacheService cacheService = (CacheService)cache.getService();
            CacheConfig cacheConfig = cacheService.getCacheConfig(cache.getPrefixedName());
            CacheConfigAccessor.setTenantControl(cacheConfig, TenantControl.NOOP_TENANT_CONTROL);
        }
    }

    @Override
    public Class<? extends Cache> getContextType() {
        return Cache.class;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 70;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.cacheName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.cacheName = in.readUTF();
    }

    @Override
    public String getDistributedObjectName() {
        return this.cacheName;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }
}

