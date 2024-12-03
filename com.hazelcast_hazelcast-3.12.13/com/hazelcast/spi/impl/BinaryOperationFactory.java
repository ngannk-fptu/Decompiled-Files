/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.instance.Node;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeAware;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import java.io.IOException;

public final class BinaryOperationFactory
implements OperationFactory,
NodeAware,
IdentifiedDataSerializable {
    private Data operationData;
    private NodeEngine nodeEngine;

    public BinaryOperationFactory() {
    }

    public BinaryOperationFactory(Operation operation, NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.operationData = nodeEngine.toData(operation);
    }

    public BinaryOperationFactory(Data operationData) {
        this.operationData = operationData;
    }

    @Override
    public Operation createOperation() {
        return (Operation)this.nodeEngine.toObject(this.operationData);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeData(this.operationData);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.operationData = in.readData();
    }

    @Override
    public void setNode(Node node) {
        this.nodeEngine = node.nodeEngine;
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 5;
    }
}

