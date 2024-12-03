/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CacheEventHandler;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataGenerator;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import java.util.UUID;

public class CacheAssignAndGetUuidsOperation
extends Operation
implements PartitionAwareOperation,
IdentifiedDataSerializable,
AllowedDuringPassiveState {
    private UUID partitionUuid;

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 52;
    }

    @Override
    public void run() throws Exception {
        this.partitionUuid = this.getMetaDataGenerator().getOrCreateUuid(this.getPartitionId());
    }

    @Override
    public Object getResponse() {
        return this.partitionUuid;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    private MetaDataGenerator getMetaDataGenerator() {
        CacheService service = (CacheService)this.getService();
        CacheEventHandler cacheEventHandler = service.getCacheEventHandler();
        return cacheEventHandler.getMetaDataGenerator();
    }
}

