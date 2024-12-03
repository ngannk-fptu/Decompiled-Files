/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.operations.BaseSignalOperation;
import com.hazelcast.concurrent.lock.operations.SignalBackupOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;

public class SignalOperation
extends BaseSignalOperation
implements BackupAwareOperation,
MutatingOperation {
    public SignalOperation() {
    }

    public SignalOperation(ObjectNamespace namespace, Data key, long threadId, String conditionId, boolean all) {
        super(namespace, key, threadId, conditionId, all);
    }

    @Override
    public void beforeRun() throws Exception {
        LockStoreImpl lockStore = this.getLockStore();
        boolean isLockOwner = lockStore.isLockedBy(this.key, this.getCallerUuid(), this.threadId);
        this.ensureLockOwner(lockStore, isLockOwner);
    }

    private void ensureLockOwner(LockStoreImpl lockStore, boolean isLockOwner) {
        if (!isLockOwner) {
            String ownerInfo = lockStore.getOwnerInfo(this.key);
            throw new IllegalMonitorStateException("Current thread is not owner of the lock! -> " + ownerInfo);
        }
    }

    @Override
    public boolean shouldBackup() {
        return this.awaitCount > 0;
    }

    @Override
    public Operation getBackupOperation() {
        return new SignalBackupOperation(this.namespace, this.key, this.threadId, this.conditionId, this.all);
    }

    @Override
    public int getId() {
        return 14;
    }
}

