/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.concurrent.atomicreference.operations.AbstractAtomicReferenceOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class ContainsOperation
extends AbstractAtomicReferenceOperation
implements ReadonlyOperation {
    private boolean returnValue;
    private Data contains;

    public ContainsOperation() {
    }

    public ContainsOperation(String name, Data contains) {
        super(name);
        this.contains = contains;
    }

    @Override
    public void run() throws Exception {
        AtomicReferenceContainer container = this.getReferenceContainer();
        this.returnValue = container.contains(this.contains);
    }

    @Override
    public Object getResponse() {
        return this.returnValue;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.contains);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.contains = in.readData();
    }
}

