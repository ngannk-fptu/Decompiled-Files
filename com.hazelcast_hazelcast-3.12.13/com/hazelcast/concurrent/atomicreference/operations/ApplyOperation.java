/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.concurrent.atomicreference.operations.AbstractAtomicReferenceOperation;
import com.hazelcast.core.IFunction;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class ApplyOperation
extends AbstractAtomicReferenceOperation
implements MutatingOperation {
    protected Data function;
    protected Data returnValue;

    public ApplyOperation() {
    }

    public ApplyOperation(String name, Data function) {
        super(name);
        this.function = function;
    }

    @Override
    public void run() throws Exception {
        NodeEngine nodeEngine = this.getNodeEngine();
        IFunction f = (IFunction)nodeEngine.toObject(this.function);
        AtomicReferenceContainer container = this.getReferenceContainer();
        Object input = nodeEngine.toObject(container.get());
        Object output = f.apply(input);
        this.returnValue = nodeEngine.toData(output);
    }

    @Override
    public Object getResponse() {
        return this.returnValue;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.function);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.function = in.readData();
    }
}

