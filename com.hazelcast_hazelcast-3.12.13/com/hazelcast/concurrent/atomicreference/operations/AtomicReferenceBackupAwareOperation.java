/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.operations.AbstractAtomicReferenceOperation;
import com.hazelcast.spi.BackupAwareOperation;

public abstract class AtomicReferenceBackupAwareOperation
extends AbstractAtomicReferenceOperation
implements BackupAwareOperation {
    protected boolean shouldBackup = true;

    public AtomicReferenceBackupAwareOperation() {
    }

    public AtomicReferenceBackupAwareOperation(String name) {
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

