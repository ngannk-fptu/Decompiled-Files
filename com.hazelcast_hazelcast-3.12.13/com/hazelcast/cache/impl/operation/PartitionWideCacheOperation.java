/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.AbstractNamedOperation;

abstract class PartitionWideCacheOperation
extends AbstractNamedOperation
implements PartitionAwareOperation,
IdentifiedDataSerializable {
    transient Object response;

    protected PartitionWideCacheOperation() {
    }

    protected PartitionWideCacheOperation(String name) {
        super(name);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }
}

