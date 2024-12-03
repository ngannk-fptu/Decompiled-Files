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

public class RemoveOperation
extends AbstractReplicatedMapOperation
implements PartitionAwareOperation,
MutatingOperation {
    private transient ReplicatedMapService service;
    private transient Data oldValue;

    public RemoveOperation() {
    }

    public RemoveOperation(String name, Data key) {
        this.name = name;
        this.key = key;
    }

    @Override
    public void run() throws Exception {
        this.service = (ReplicatedMapService)this.getService();
        ReplicatedRecordStore store = this.service.getReplicatedRecordStore(this.name, true, this.getPartitionId());
        Object removed = store.remove(this.key);
        this.oldValue = this.getNodeEngine().toData(removed);
        this.response = new VersionResponsePair(removed, store.getVersion());
        Address thisAddress = this.getNodeEngine().getThisAddress();
        if (!this.getCallerAddress().equals(thisAddress)) {
            this.sendUpdateCallerOperation(true);
        }
    }

    @Override
    public void afterRun() throws Exception {
        this.sendReplicationOperation(true);
        ReplicatedMapEventPublishingService eventPublishingService = this.service.getEventPublishingService();
        eventPublishingService.fireEntryListenerEvent(this.key, this.oldValue, null, this.name, this.getCallerAddress());
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeData(this.key);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.key = in.readData();
    }

    @Override
    public int getId() {
        return 7;
    }
}

