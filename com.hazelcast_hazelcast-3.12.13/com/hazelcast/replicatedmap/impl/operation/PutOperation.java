/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.ReplicatedMapEventPublishingService;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractReplicatedMapOperation;
import com.hazelcast.replicatedmap.impl.operation.VersionResponsePair;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PutOperation
extends AbstractReplicatedMapOperation
implements PartitionAwareOperation,
MutatingOperation {
    private transient ReplicatedMapService service;
    private transient Data oldValue;

    public PutOperation() {
    }

    public PutOperation(String name, Data key, Data value) {
        this.name = name;
        this.key = key;
        this.value = value;
    }

    public PutOperation(String name, Data key, Data value, long ttl) {
        this.name = name;
        this.key = key;
        this.value = value;
        this.ttl = ttl;
    }

    @Override
    public void run() throws Exception {
        this.service = (ReplicatedMapService)this.getService();
        ReplicatedRecordStore store = this.service.getReplicatedRecordStore(this.name, true, this.getPartitionId());
        Address thisAddress = this.getNodeEngine().getThisAddress();
        boolean isLocal = this.getCallerAddress().equals(thisAddress);
        Object putResult = store.put(this.key, this.value, this.ttl, TimeUnit.MILLISECONDS, isLocal);
        this.oldValue = this.getNodeEngine().toData(putResult);
        this.response = new VersionResponsePair(putResult, store.getVersion());
        if (!isLocal) {
            this.sendUpdateCallerOperation(false);
        }
    }

    @Override
    public void afterRun() throws Exception {
        this.sendReplicationOperation(false);
        ReplicatedMapEventPublishingService eventPublishingService = this.service.getEventPublishingService();
        eventPublishingService.fireEntryListenerEvent(this.key, this.oldValue, this.value, this.name, this.getCallerAddress());
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeData(this.key);
        out.writeData(this.value);
        out.writeLong(this.ttl);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.key = in.readData();
        this.value = in.readData();
        this.ttl = in.readLong();
    }

    @Override
    public int getId() {
        return 6;
    }
}

