/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.AbstractMapOperationFactory;
import com.hazelcast.map.impl.operation.AddIndexOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class AddIndexOperationFactory
extends AbstractMapOperationFactory {
    private String name;
    private String attributeName;
    private boolean ordered;

    public AddIndexOperationFactory() {
    }

    public AddIndexOperationFactory(String name, String attributeName, boolean ordered) {
        this.name = name;
        this.attributeName = attributeName;
        this.ordered = ordered;
    }

    @Override
    public Operation createOperation() {
        return new AddIndexOperation(this.name, this.attributeName, this.ordered);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.attributeName);
        out.writeBoolean(this.ordered);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.attributeName = in.readUTF();
        this.ordered = in.readBoolean();
    }

    @Override
    public int getId() {
        return 75;
    }
}

