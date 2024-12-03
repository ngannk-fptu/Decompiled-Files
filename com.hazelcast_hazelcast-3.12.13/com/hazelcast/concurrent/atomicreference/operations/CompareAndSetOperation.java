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

public class CompareAndSetOperation
extends AtomicReferenceBackupAwareOperation
implements MutatingOperation {
    private Data expect;
    private Data update;
    private boolean returnValue;

    public CompareAndSetOperation() {
    }

    public CompareAndSetOperation(String name, Data expect, Data update) {
        super(name);
        this.expect = expect;
        this.update = update;
    }

    @Override
    public void run() throws Exception {
        AtomicReferenceContainer container = this.getReferenceContainer();
        this.shouldBackup = this.returnValue = container.compareAndSet(this.expect, this.update);
    }

    @Override
    public Object getResponse() {
        return this.returnValue;
    }

    @Override
    public boolean shouldBackup() {
        return this.shouldBackup;
    }

    @Override
    public Operation getBackupOperation() {
        return new SetBackupOperation(this.name, this.update);
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.expect);
        out.writeData(this.update);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.expect = in.readData();
        this.update = in.readData();
    }
}

