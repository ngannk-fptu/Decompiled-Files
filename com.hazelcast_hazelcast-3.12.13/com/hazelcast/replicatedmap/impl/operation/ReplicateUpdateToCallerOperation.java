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
import com.hazelcast.replicatedmap.impl.operation.AbstractSerializableOperation;
import com.hazelcast.replicatedmap.impl.operation.VersionResponsePair;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ReplicateUpdateToCallerOperation
extends AbstractSerializableOperation
implements PartitionAwareOperation {
    private String name;
    private long callId;
    private Data dataKey;
    private Data dataValue;
    private VersionResponsePair response;
    private long ttl;
    private boolean isRemove;

    public ReplicateUpdateToCallerOperation() {
    }

    public ReplicateUpdateToCallerOperation(String name, long callId, Data dataKey, Data dataValue, VersionResponsePair response, long ttl, boolean isRemove) {
        this.name = name;
        this.callId = callId;
        this.dataKey = dataKey;
        this.dataValue = dataValue;
        this.response = response;
        this.ttl = ttl;
        this.isRemove = isRemove;
    }

    @Override
    public void run() throws Exception {
        long updateVersion;
        ILogger logger = this.getLogger();
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        ReplicatedRecordStore store = service.getReplicatedRecordStore(this.name, true, this.getPartitionId());
        long currentVersion = store.getVersion();
        if (currentVersion >= (updateVersion = this.response.getVersion())) {
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
            store.putWithVersion(key, value, this.ttl, TimeUnit.MILLISECONDS, true, updateVersion);
        }
        this.publishEvent();
    }

    @Override
    public void afterRun() throws Exception {
        this.notifyCaller();
    }

    private void publishEvent() {
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        ReplicatedMapEventPublishingService eventPublishingService = service.getEventPublishingService();
        Address thisAddress = this.getNodeEngine().getThisAddress();
        Data dataOldValue = this.getNodeEngine().toData(this.response.getResponse());
        if (this.isRemove) {
            eventPublishingService.fireEntryListenerEvent(this.dataKey, dataOldValue, null, this.name, thisAddress);
        } else {
            eventPublishingService.fireEntryListenerEvent(this.dataKey, dataOldValue, this.dataValue, this.name, thisAddress);
        }
    }

    private void notifyCaller() {
        OperationServiceImpl operationService = (OperationServiceImpl)this.getNodeEngine().getOperationService();
        operationService.getBackupHandler().notifyBackupComplete(this.callId);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeLong(this.callId);
        out.writeData(this.dataKey);
        out.writeData(this.dataValue);
        this.response.writeData(out);
        out.writeLong(this.ttl);
        out.writeBoolean(this.isRemove);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.callId = in.readLong();
        this.dataKey = in.readData();
        this.dataValue = in.readData();
        this.response = new VersionResponsePair();
        this.response.readData(in);
        this.ttl = in.readLong();
        this.isRemove = in.readBoolean();
    }

    @Override
    public int getId() {
        return 4;
    }
}

