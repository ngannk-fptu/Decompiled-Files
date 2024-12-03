/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.operations.AbstractAtomicLongOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class AddBackupOperation
extends AbstractAtomicLongOperation
implements BackupOperation {
    private long delta;

    public AddBackupOperation() {
    }

    public AddBackupOperation(String name, long delta) {
        super(name);
        this.delta = delta;
    }

    @Override
    public void run() throws Exception {
        AtomicLongContainer container = this.getLongContainer();
        container.addAndGet(this.delta);
    }

    @Override
    public int getId() {
        return 0;
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

