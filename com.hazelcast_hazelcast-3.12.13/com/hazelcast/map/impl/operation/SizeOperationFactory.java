/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.AbstractMapOperationFactory;
import com.hazelcast.map.impl.operation.MapSizeOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class SizeOperationFactory
extends AbstractMapOperationFactory {
    private String name;

    public SizeOperationFactory() {
    }

    public SizeOperationFactory(String name) {
        this.name = name;
    }

    @Override
    public Operation createOperation() {
        return new MapSizeOperation(this.name);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
    }

    @Override
    public int getId() {
        return 89;
    }
}

