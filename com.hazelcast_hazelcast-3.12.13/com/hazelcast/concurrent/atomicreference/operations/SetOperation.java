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

public class SetOperation
extends AtomicReferenceBackupAwareOperation
implements MutatingOperation {
    private Data newValue;

    public SetOperation() {
    }

    public SetOperation(String name, Data newValue) {
        super(name);
        this.newValue = newValue;
    }

    @Override
    public void run() throws Exception {
        AtomicReferenceContainer container = this.getReferenceContainer();
        container.set(this.newValue);
    }

    @Override
    public Operation getBackupOperation() {
        return new SetBackupOperation(this.name, this.newValue);
    }

    @Override
    public int getId() {
        return 11;
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

