/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.PreJoinCacheConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class AddCacheConfigOperation
extends Operation
implements IdentifiedDataSerializable {
    private PreJoinCacheConfig cacheConfig;

    public AddCacheConfigOperation() {
    }

    public AddCacheConfigOperation(PreJoinCacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    @Override
    public void run() {
        ICacheService cacheService = (ICacheService)this.getService();
        cacheService.setTenantControl(this.cacheConfig);
        cacheService.putCacheConfigIfAbsent(this.cacheConfig);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeObject(this.cacheConfig);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.cacheConfig = (PreJoinCacheConfig)in.readObject();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 66;
    }
}

