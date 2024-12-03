/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.AbstractMapOperationFactory;
import com.hazelcast.map.impl.operation.KeyLoadStatusOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class KeyLoadStatusOperationFactory
extends AbstractMapOperationFactory {
    private Throwable exception;
    private String name;

    public KeyLoadStatusOperationFactory() {
    }

    public KeyLoadStatusOperationFactory(String name, Throwable exception) {
        this.name = name;
        this.exception = exception;
    }

    @Override
    public Operation createOperation() {
        return new KeyLoadStatusOperation(this.name, this.exception);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeObject(this.exception);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.exception = (Throwable)in.readObject();
    }

    @Override
    public int getId() {
        return 81;
    }
}

