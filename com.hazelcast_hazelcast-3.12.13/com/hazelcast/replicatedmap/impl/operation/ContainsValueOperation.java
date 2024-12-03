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
import java.util.Collection;

public class ContainsValueOperation
extends AbstractNamedSerializableOperation
implements ReadonlyOperation {
    private String name;
    private Data value;
    private transient boolean response;

    public ContainsValueOperation() {
    }

    public ContainsValueOperation(String name, Data value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        Collection<ReplicatedRecordStore> stores = service.getAllReplicatedRecordStores(this.name);
        for (ReplicatedRecordStore store : stores) {
            if (!store.containsValue(this.value)) continue;
            this.response = true;
            break;
        }
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.value = in.readData();
    }

    @Override
    public int getId() {
        return 14;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

