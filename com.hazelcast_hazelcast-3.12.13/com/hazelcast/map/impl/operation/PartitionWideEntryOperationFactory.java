/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.operation.AbstractMapOperationFactory;
import com.hazelcast.map.impl.operation.PartitionWideEntryOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class PartitionWideEntryOperationFactory
extends AbstractMapOperationFactory {
    private String name;
    private EntryProcessor entryProcessor;

    public PartitionWideEntryOperationFactory() {
    }

    public PartitionWideEntryOperationFactory(String name, EntryProcessor entryProcessor) {
        this.name = name;
        this.entryProcessor = entryProcessor;
    }

    @Override
    public Operation createOperation() {
        return new PartitionWideEntryOperation(this.name, this.entryProcessor);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeObject(this.entryProcessor);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.entryProcessor = (EntryProcessor)in.readObject();
    }

    @Override
    public int getId() {
        return 85;
    }
}

