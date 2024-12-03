/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.operation.AbstractMapOperationFactory;
import com.hazelcast.map.impl.operation.MultipleEntryOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

public class MultipleEntryOperationFactory
extends AbstractMapOperationFactory {
    private String name;
    private Set<Data> keys;
    private EntryProcessor entryProcessor;

    public MultipleEntryOperationFactory() {
    }

    public MultipleEntryOperationFactory(String name, Set<Data> keys, EntryProcessor entryProcessor) {
        this.name = name;
        this.keys = keys;
        this.entryProcessor = entryProcessor;
    }

    @Override
    public Operation createOperation() {
        return new MultipleEntryOperation(this.name, this.keys, this.entryProcessor);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.keys.size());
        for (Data key : this.keys) {
            out.writeData(key);
        }
        out.writeObject(this.entryProcessor);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        int size = in.readInt();
        this.keys = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            Data key = in.readData();
            this.keys.add(key);
        }
        this.entryProcessor = (EntryProcessor)in.readObject();
    }

    @Override
    public int getId() {
        return 90;
    }
}

