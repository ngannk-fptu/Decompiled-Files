/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.operations.AddBackupOperation;
import com.hazelcast.concurrent.atomiclong.operations.AtomicLongBackupAwareOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class AddAndGetOperation
extends AtomicLongBackupAwareOperation
implements MutatingOperation {
    private long delta;
    private long returnValue;

    public AddAndGetOperation() {
    }

    public AddAndGetOperation(String name, long delta) {
        super(name);
        this.delta = delta;
    }

    @Override
    public void run() throws Exception {
        AtomicLongContainer container = this.getLongContainer();
        this.returnValue = container.addAndGet(this.delta);
    }

    @Override
    public Object getResponse() {
        return this.returnValue;
    }

    @Override
    public Operation getBackupOperation() {
        return new AddBackupOperation(this.name, this.delta);
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.delta);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.delta = in.readLong();
    }
}

