/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.internal.nearcache.impl.invalidation.Invalidator;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataGenerator;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.nearcache.MapNearCacheManager;
import com.hazelcast.map.impl.operation.MapReplicationOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.ServiceNamespace;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MapNearCacheStateHolder
implements IdentifiedDataSerializable {
    protected UUID partitionUuid;
    protected List<Object> mapNameSequencePairs = Collections.emptyList();
    private MapReplicationOperation mapReplicationOperation;

    public MapNearCacheStateHolder() {
    }

    public MapNearCacheStateHolder(MapReplicationOperation mapReplicationOperation) {
        this.mapReplicationOperation = mapReplicationOperation;
    }

    void prepare(PartitionContainer container, Collection<ServiceNamespace> namespaces, int replicaIndex) {
        MapService mapService = container.getMapService();
        MetaDataGenerator metaData = this.getPartitionMetaDataGenerator(mapService);
        int partitionId = container.getPartitionId();
        this.partitionUuid = metaData.getOrCreateUuid(partitionId);
        for (ServiceNamespace namespace : namespaces) {
            if (this.mapNameSequencePairs == Collections.emptyList()) {
                this.mapNameSequencePairs = new ArrayList<Object>(namespaces.size());
            }
            ObjectNamespace mapNamespace = (ObjectNamespace)namespace;
            String mapName = mapNamespace.getObjectName();
            this.mapNameSequencePairs.add(mapName);
            this.mapNameSequencePairs.add(metaData.currentSequence(mapName, partitionId));
        }
    }

    private MetaDataGenerator getPartitionMetaDataGenerator(MapService mapService) {
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        MapNearCacheManager mapNearCacheManager = mapServiceContext.getMapNearCacheManager();
        Invalidator invalidator = mapNearCacheManager.getInvalidator();
        return invalidator.getMetaDataGenerator();
    }

    void applyState() {
        MapService mapService = (MapService)this.mapReplicationOperation.getService();
        MetaDataGenerator metaDataGenerator = this.getPartitionMetaDataGenerator(mapService);
        int partitionId = this.mapReplicationOperation.getPartitionId();
        if (this.partitionUuid != null) {
            metaDataGenerator.setUuid(partitionId, this.partitionUuid);
        }
        int i = 0;
        while (i < this.mapNameSequencePairs.size()) {
            String mapName = (String)this.mapNameSequencePairs.get(i++);
            long sequence = (Long)this.mapNameSequencePairs.get(i++);
            metaDataGenerator.setCurrentSequence(mapName, partitionId, sequence);
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        boolean nullUuid = this.partitionUuid == null;
        out.writeBoolean(nullUuid);
        if (!nullUuid) {
            out.writeLong(this.partitionUuid.getMostSignificantBits());
            out.writeLong(this.partitionUuid.getLeastSignificantBits());
        }
        out.writeInt(this.mapNameSequencePairs.size());
        for (Object item : this.mapNameSequencePairs) {
            out.writeObject(item);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        boolean nullUuid = in.readBoolean();
        this.partitionUuid = nullUuid ? null : new UUID(in.readLong(), in.readLong());
        int size = in.readInt();
        this.mapNameSequencePairs = new ArrayList<Object>(size);
        for (int i = 0; i < size; ++i) {
            this.mapNameSequencePairs.add(in.readObject());
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 120;
    }
}

