/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.operation.MapNearCacheStateHolder;
import com.hazelcast.map.impl.operation.MapReplicationStateHolder;
import com.hazelcast.map.impl.operation.WriteBehindStateHolder;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.RecordInfo;
import com.hazelcast.map.impl.record.RecordReplicationInfo;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.util.Collection;

public class MapReplicationOperation
extends Operation
implements IdentifiedDataSerializable,
Versioned {
    protected final MapReplicationStateHolder mapReplicationStateHolder = new MapReplicationStateHolder(this);
    protected final WriteBehindStateHolder writeBehindStateHolder = new WriteBehindStateHolder(this);
    protected final MapNearCacheStateHolder mapNearCacheStateHolder = new MapNearCacheStateHolder(this);

    public MapReplicationOperation() {
    }

    public MapReplicationOperation(PartitionContainer container, int partitionId, int replicaIndex) {
        this.setPartitionId(partitionId).setReplicaIndex(replicaIndex);
        Collection<ServiceNamespace> namespaces = container.getAllNamespaces(replicaIndex);
        this.mapReplicationStateHolder.prepare(container, namespaces, replicaIndex);
        this.writeBehindStateHolder.prepare(container, namespaces, replicaIndex);
        this.mapNearCacheStateHolder.prepare(container, namespaces, replicaIndex);
    }

    public MapReplicationOperation(PartitionContainer container, Collection<ServiceNamespace> namespaces, int partitionId, int replicaIndex) {
        this.setPartitionId(partitionId).setReplicaIndex(replicaIndex);
        this.mapReplicationStateHolder.prepare(container, namespaces, replicaIndex);
        this.writeBehindStateHolder.prepare(container, namespaces, replicaIndex);
        this.mapNearCacheStateHolder.prepare(container, namespaces, replicaIndex);
    }

    @Override
    public void run() {
        this.mapReplicationStateHolder.applyState();
        this.writeBehindStateHolder.applyState();
        if (this.getReplicaIndex() == 0) {
            this.mapNearCacheStateHolder.applyState();
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        this.mapReplicationStateHolder.writeData(out);
        this.writeBehindStateHolder.writeData(out);
        this.mapNearCacheStateHolder.writeData(out);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.mapReplicationStateHolder.readData(in);
        this.writeBehindStateHolder.readData(in);
        this.mapNearCacheStateHolder.readData(in);
    }

    RecordReplicationInfo toReplicationInfo(Record record, SerializationService ss) {
        RecordInfo info = Records.buildRecordInfo(record);
        Object dataValue = ss.toData(record.getValue());
        return new RecordReplicationInfo(record.getKey(), (Data)dataValue, info);
    }

    RecordStore getRecordStore(String mapName) {
        boolean skipLoadingOnRecordStoreCreate = true;
        MapService mapService = (MapService)this.getService();
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        return mapServiceContext.getRecordStore(this.getPartitionId(), mapName, true);
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 96;
    }
}

