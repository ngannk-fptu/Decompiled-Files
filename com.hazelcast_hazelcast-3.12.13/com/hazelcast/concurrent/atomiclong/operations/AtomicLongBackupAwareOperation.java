/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.operations.AbstractAtomicLongOperation;
import com.hazelcast.spi.BackupAwareOperation;

public abstract class AtomicLongBackupAwareOperation
extends AbstractAtomicLongOperation
implements BackupAwareOperation {
    protected boolean shouldBackup = true;

    public AtomicLongBackupAwareOperation() {
    }

    public AtomicLongBackupAwareOperation(String name) {
        super(name);
    }

    @Override
    public boolean shouldBackup() {
        return this.shouldBackup;
    }

    @Override
    public int getSyncBackupCount() {
        return 1;
    }

    @Override
    public int getAsyncBackupCount() {
        return 0;
    }
}

