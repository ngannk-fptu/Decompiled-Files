/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.wan.MapReplicationRemove;
import com.hazelcast.map.impl.wan.MapReplicationUpdate;
import com.hazelcast.map.impl.wan.WanMapEntryView;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ReplicationSupportingService;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.wan.ReplicationEventObject;
import com.hazelcast.wan.WanReplicationEvent;
import com.hazelcast.wan.impl.DistributedServiceWanEventCounters;

class MapReplicationSupportingService
implements ReplicationSupportingService {
    private final MapServiceContext mapServiceContext;
    private final NodeEngine nodeEngine;
    private final DistributedServiceWanEventCounters wanEventTypeCounters;

    MapReplicationSupportingService(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
        this.nodeEngine = mapServiceContext.getNodeEngine();
        this.wanEventTypeCounters = this.nodeEngine.getWanReplicationService().getReceivedEventCounters("hz:impl:mapService");
    }

    @Override
    public void onReplicationEvent(WanReplicationEvent replicationEvent) {
        ReplicationEventObject eventObject = replicationEvent.getEventObject();
        if (eventObject instanceof MapReplicationUpdate) {
            this.handleUpdate((MapReplicationUpdate)eventObject);
        } else if (eventObject instanceof MapReplicationRemove) {
            this.handleRemove((MapReplicationRemove)eventObject);
        }
    }

    private void handleRemove(MapReplicationRemove replicationRemove) {
        String mapName = replicationRemove.getMapName();
        MapOperationProvider operationProvider = this.mapServiceContext.getMapOperationProvider(mapName);
        MapOperation operation = operationProvider.createRemoveOperation(replicationRemove.getMapName(), replicationRemove.getKey(), true);
        try {
            int partitionId = this.nodeEngine.getPartitionService().getPartitionId(replicationRemove.getKey());
            InternalCompletableFuture future = this.nodeEngine.getOperationService().invokeOnPartition("hz:impl:mapService", operation, partitionId);
            future.get();
            this.wanEventTypeCounters.incrementRemove(mapName);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private void handleUpdate(MapReplicationUpdate replicationUpdate) {
        MapOperation operation;
        Object mergePolicy = replicationUpdate.getMergePolicy();
        String mapName = replicationUpdate.getMapName();
        MapOperationProvider operationProvider = this.mapServiceContext.getMapOperationProvider(mapName);
        if (mergePolicy instanceof SplitBrainMergePolicy) {
            SerializationService serializationService = this.nodeEngine.getSerializationService();
            SplitBrainMergeTypes.MapMergeTypes mergingEntry = MergingValueFactory.createMergingEntry(serializationService, replicationUpdate.getEntryView());
            operation = operationProvider.createMergeOperation(mapName, mergingEntry, (SplitBrainMergePolicy)mergePolicy, true);
        } else {
            WanMapEntryView<Data, Data> entryView = replicationUpdate.getEntryView();
            operation = operationProvider.createLegacyMergeOperation(mapName, entryView, (MapMergePolicy)mergePolicy, true);
        }
        try {
            int partitionId = this.nodeEngine.getPartitionService().getPartitionId(replicationUpdate.getEntryView().getKey());
            InternalCompletableFuture future = this.nodeEngine.getOperationService().invokeOnPartition("hz:impl:mapService", operation, partitionId);
            future.get();
            this.wanEventTypeCounters.incrementUpdate(mapName);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }
}

