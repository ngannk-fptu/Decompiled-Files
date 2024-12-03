/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.concurrent.atomicreference.operations.AtomicReferenceBackupAwareOperation;
import com.hazelcast.concurrent.atomicreference.operations.SetBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class GetAndSetOperation
extends AtomicReferenceBackupAwareOperation
implements MutatingOperation {
    private Data newValue;
    private Data returnValue;

    public GetAndSetOperation() {
    }

    public GetAndSetOperation(String name, Data newValue) {
        super(name);
        this.newValue = newValue;
    }

    @Override
    public void run() throws Exception {
        AtomicReferenceContainer container = this.getReferenceContainer();
        this.returnValue = container.getAndSet(this.newValue);
    }

    @Override
    public Object getResponse() {
        return this.returnValue;
    }

    @Override
    public Operation getBackupOperation() {
        return new SetBackupOperation(this.name, this.newValue);
    }

    @Override
    public int getId() {
        return 6;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.newValue);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.newValue = in.readData();
    }
}

