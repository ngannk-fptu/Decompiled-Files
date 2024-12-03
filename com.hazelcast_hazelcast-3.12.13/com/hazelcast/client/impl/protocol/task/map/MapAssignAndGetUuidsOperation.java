/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.internal.nearcache.impl.invalidation.Invalidator;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataGenerator;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.nearcache.MapNearCacheManager;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import java.util.UUID;

public class MapAssignAndGetUuidsOperation
extends Operation
implements PartitionAwareOperation,
IdentifiedDataSerializable,
AllowedDuringPassiveState {
    private UUID partitionUuid;

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
        return "hz:impl:mapService";
    }

    private MetaDataGenerator getMetaDataGenerator() {
        MapService mapService = (MapService)this.getService();
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        MapNearCacheManager mapNearCacheManager = mapServiceContext.getMapNearCacheManager();
        Invalidator invalidator = mapNearCacheManager.getInvalidator();
        return invalidator.getMetaDataGenerator();
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 121;
    }
}

