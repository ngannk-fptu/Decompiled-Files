/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.operations.AtomicLongBackupAwareOperation;
import com.hazelcast.concurrent.atomiclong.operations.SetBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class CompareAndSetOperation
extends AtomicLongBackupAwareOperation
implements MutatingOperation {
    private long expect;
    private long update;
    private boolean returnValue;

    public CompareAndSetOperation() {
    }

    public CompareAndSetOperation(String name, long expect, long update) {
        super(name);
        this.expect = expect;
        this.update = update;
    }

    @Override
    public void run() throws Exception {
        AtomicLongContainer container = this.getLongContainer();
        this.shouldBackup = this.returnValue = container.compareAndSet(this.expect, this.update);
    }

    @Override
    public Object getResponse() {
        return this.returnValue;
    }

    @Override
    public Operation getBackupOperation() {
        return new SetBackupOperation(this.name, this.update);
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.expect);
        out.writeLong(this.update);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.expect = in.readLong();
        this.update = in.readLong();
    }
}

