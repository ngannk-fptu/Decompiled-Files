/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;
import java.util.Collection;

public class IsEmptyOperation
extends AbstractNamedSerializableOperation
implements ReadonlyOperation {
    private String name;
    private transient boolean response;

    public IsEmptyOperation() {
    }

    public IsEmptyOperation(String name) {
        this.name = name;
    }

    @Override
    public void run() throws Exception {
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        Collection<ReplicatedRecordStore> stores = service.getAllReplicatedRecordStores(this.name);
        for (ReplicatedRecordStore store : stores) {
            if (store.isEmpty()) continue;
            this.response = false;
            return;
        }
        this.response = true;
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
    }

    @Override
    public int getId() {
        return 17;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

