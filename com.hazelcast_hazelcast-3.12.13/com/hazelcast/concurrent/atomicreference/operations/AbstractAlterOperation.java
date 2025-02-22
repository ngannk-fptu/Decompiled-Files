/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.operations.AtomicReferenceBackupAwareOperation;
import com.hazelcast.concurrent.atomicreference.operations.SetBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public abstract class AbstractAlterOperation
extends AtomicReferenceBackupAwareOperation
implements MutatingOperation {
    protected Data function;
    protected Object response;
    protected Data backup;

    public AbstractAlterOperation() {
    }

    public AbstractAlterOperation(String name, Data function) {
        super(name);
        this.function = function;
    }

    protected boolean isEquals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        if (o1 == o2) {
            return true;
        }
        return o1.equals(o2);
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public Operation getBackupOperation() {
        return new SetBackupOperation(this.name, this.backup);
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

