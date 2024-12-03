/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.concurrent.atomicreference.operations.AbstractAlterOperation;
import com.hazelcast.core.IFunction;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;

public class AlterAndGetOperation
extends AbstractAlterOperation {
    public AlterAndGetOperation() {
    }

    public AlterAndGetOperation(String name, Data function) {
        super(name, function);
    }

    @Override
    public void run() throws Exception {
        Object input;
        Object output;
        Data serializedOutput;
        NodeEngine nodeEngine = this.getNodeEngine();
        IFunction f = (IFunction)nodeEngine.toObject(this.function);
        AtomicReferenceContainer container = this.getReferenceContainer();
        Data originalData = container.get();
        boolean bl = this.shouldBackup = !this.isEquals(originalData, serializedOutput = nodeEngine.toData(output = f.apply(input = nodeEngine.toObject(originalData))));
        if (this.shouldBackup) {
            this.backup = serializedOutput;
            container.set(this.backup);
        }
        this.response = output;
    }

    @Override
    public int getId() {
        return 0;
    }
}

