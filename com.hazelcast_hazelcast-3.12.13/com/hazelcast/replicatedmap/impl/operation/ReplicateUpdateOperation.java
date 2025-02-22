/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.ReplicatedMapEventPublishingService;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.operation.VersionResponsePair;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.PartitionAwareOperation;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ReplicateUpdateOperation
extends AbstractNamedSerializableOperation
implements PartitionAwareOperation {
    private VersionResponsePair response;
    private boolean isRemove;
    private String name;
    private Data dataKey;
    private Data dataValue;
    private long ttl;
    private Address origin;

    public ReplicateUpdateOperation() {
    }

    public ReplicateUpdateOperation(String name, Data dataKey, Data dataValue, long ttl, VersionResponsePair response, boolean isRemove, Address origin) {
        this.name = name;
        this.dataKey = dataKey;
        this.dataValue = dataValue;
        this.ttl = ttl;
        this.response = response;
        this.isRemove = isRemove;
        this.origin = origin;
    }

    @Override
    public void run() throws Exception {
        long updateVersion;
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        ReplicatedRecordStore store = service.getReplicatedRecordStore(this.name, true, this.getPartitionId());
        long currentVersion = store.getVersion();
        if (currentVersion >= (updateVersion = this.response.getVersion())) {
            ILogger logger = this.getLogger();
            if (logger.isFineEnabled()) {
                logger.fine("Rejecting stale update received for replicated map '" + this.name + "' (partitionId " + this.getPartitionId() + ") (current version " + currentVersion + ") (update version " + updateVersion + ")");
            }
            return;
        }
        Object key = store.marshall(this.dataKey);
        Object value = store.marshall(this.dataValue);
        if (this.isRemove) {
            store.removeWithVersion(key, updateVersion);
        } else {
            store.putWithVersion(key, value, this.ttl, TimeUnit.MILLISECONDS, false, updateVersion);
        }
        this.publishEvent();
    }

    private void publishEvent() {
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        ReplicatedMapEventPublishingService eventPublishingService = service.getEventPublishingService();
        Data dataOldValue = this.getNodeEngine().toData(this.response.getResponse());
        if (this.isRemove) {
            eventPublishingService.fireEntryListenerEvent(this.dataKey, dataOldValue, null, this.name, this.origin);
        } else {
            eventPublishingService.fireEntryListenerEvent(this.dataKey, dataOldValue, this.dataValue, this.name, this.origin);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        this.response.writeData(out);
        out.writeUTF(this.name);
        out.writeData(this.dataKey);
        out.writeData(this.dataValue);
        out.writeLong(this.ttl);
        out.writeBoolean(this.isRemove);
        out.writeObject(this.origin);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.response = new VersionResponsePair();
        this.response.readData(in);
        this.name = in.readUTF();
        this.dataKey = in.readData();
        this.dataValue = in.readData();
        this.ttl = in.readLong();
        this.isRemove = in.readBoolean();
        this.origin = (Address)in.readObject();
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

