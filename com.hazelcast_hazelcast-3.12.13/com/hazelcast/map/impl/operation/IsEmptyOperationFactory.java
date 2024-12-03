/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.AbstractMapOperationFactory;
import com.hazelcast.map.impl.operation.MapIsEmptyOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class IsEmptyOperationFactory
extends AbstractMapOperationFactory {
    private String name;

    public IsEmptyOperationFactory() {
    }

    public IsEmptyOperationFactory(String name) {
        this.name = name;
    }

    @Override
    public Operation createOperation() {
        return new MapIsEmptyOperation(this.name);
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
        return 80;
    }
}

