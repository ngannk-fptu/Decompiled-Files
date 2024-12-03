/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.concurrent.atomicreference.operations.AbstractAlterOperation;
import com.hazelcast.core.IFunction;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;

public class GetAndAlterOperation
extends AbstractAlterOperation {
    public GetAndAlterOperation() {
    }

    public GetAndAlterOperation(String name, Data function) {
        super(name, function);
    }

    @Override
    public void run() throws Exception {
        NodeEngine nodeEngine = this.getNodeEngine();
        IFunction f = (IFunction)nodeEngine.toObject(this.function);
        AtomicReferenceContainer container = this.getReferenceContainer();
        this.response = container.get();
        Object input = nodeEngine.toObject(container.get());
        Object output = f.apply(input);
        Data serializedOutput = nodeEngine.toData(output);
        boolean bl = this.shouldBackup = !this.isEquals(this.response, serializedOutput);
        if (this.shouldBackup) {
            container.set(serializedOutput);
            this.backup = serializedOutput;
        }
    }

    @Override
    public int getId() {
        return 5;
    }
}

