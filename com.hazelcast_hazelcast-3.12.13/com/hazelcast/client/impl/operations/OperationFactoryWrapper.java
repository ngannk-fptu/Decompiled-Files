/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.operations;

import com.hazelcast.client.impl.ClientDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import java.io.IOException;

public final class OperationFactoryWrapper
implements OperationFactory {
    private OperationFactory opFactory;
    private String uuid;

    public OperationFactoryWrapper() {
    }

    public OperationFactoryWrapper(OperationFactory opFactory, String uuid) {
        this.opFactory = opFactory;
        this.uuid = uuid;
    }

    @Override
    public Operation createOperation() {
        Operation op = this.opFactory.createOperation();
        op.setCallerUuid(this.uuid);
        return op;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.uuid);
        out.writeObject(this.opFactory);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.uuid = in.readUTF();
        this.opFactory = (OperationFactory)in.readObject();
    }

    public OperationFactory getOperationFactory() {
        return this.opFactory;
    }

    public String getUuid() {
        return this.uuid;
    }

    @Override
    public int getFactoryId() {
        return ClientDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 4;
    }

    public String toString() {
        return "OperationFactoryWrapper{opFactory=" + this.opFactory + ", uuid='" + this.uuid + '\'' + '}';
    }
}

