/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapEntries;
import com.hazelcast.replicatedmap.impl.operation.PutAllOperation;
import com.hazelcast.replicatedmap.impl.operation.ReplicatedMapDataSerializerHook;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import java.io.IOException;

public class PutAllOperationFactory
implements OperationFactory {
    private String name;
    private ReplicatedMapEntries entries;

    public PutAllOperationFactory() {
    }

    public PutAllOperationFactory(String name, ReplicatedMapEntries entries) {
        this.name = name;
        this.entries = entries;
    }

    @Override
    public Operation createOperation() {
        return new PutAllOperation(this.name, this.entries).setServiceName("hz:impl:replicatedMapService");
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeObject(this.entries);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.entries = (ReplicatedMapEntries)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return ReplicatedMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 24;
    }
}

