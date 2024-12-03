/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class GetOperation
extends AbstractNamedSerializableOperation
implements ReadonlyOperation {
    private String name;
    private Data key;
    private transient Object response;

    public GetOperation() {
    }

    public GetOperation(String name, Data key) {
        this.name = name;
        this.key = key;
    }

    @Override
    public void run() throws Exception {
        ReplicatedRecord record;
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        ReplicatedRecordStore store = service.getReplicatedRecordStore(this.name, false, this.getPartitionId());
        if (store != null && (record = store.getReplicatedRecord(this.key)) != null) {
            this.response = record.getValue();
        }
    }

    @Override
    public Object getResponse() {
        return this.response;
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
        return 11;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

