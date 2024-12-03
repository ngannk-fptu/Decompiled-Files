/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class ContainsKeyOperation
extends AbstractNamedSerializableOperation
implements ReadonlyOperation {
    private String name;
    private Data key;
    private transient boolean response;

    public ContainsKeyOperation() {
    }

    public ContainsKeyOperation(String name, Data key) {
        this.name = name;
        this.key = key;
    }

    @Override
    public void run() throws Exception {
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        ReplicatedRecordStore store = service.getReplicatedRecordStore(this.name, false, this.getPartitionId());
        this.response = store != null && store.containsKey(this.key);
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
        return 13;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

