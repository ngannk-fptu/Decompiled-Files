/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.operations.SemaphoreOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public abstract class SemaphoreBackupOperation
extends SemaphoreOperation
implements BackupOperation {
    protected String firstCaller;

    protected SemaphoreBackupOperation() {
    }

    protected SemaphoreBackupOperation(String name, int permitCount, String firstCaller) {
        super(name, permitCount);
        this.firstCaller = firstCaller;
    }

    @Override
    public void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.firstCaller);
    }

    @Override
    public void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.firstCaller = in.readUTF();
    }
}

