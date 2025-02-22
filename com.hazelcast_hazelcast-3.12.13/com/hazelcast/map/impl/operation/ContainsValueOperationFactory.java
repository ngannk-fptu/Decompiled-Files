/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.AbstractMapOperationFactory;
import com.hazelcast.map.impl.operation.ContainsValueOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public final class ContainsValueOperationFactory
extends AbstractMapOperationFactory {
    private String name;
    private Data value;

    public ContainsValueOperationFactory() {
    }

    public ContainsValueOperationFactory(String name, Data value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Operation createOperation() {
        return new ContainsValueOperation(this.name, this.value);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeData(this.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.value = in.readData();
    }

    @Override
    public int getId() {
        return 78;
    }
}

