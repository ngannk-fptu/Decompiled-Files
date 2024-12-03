/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.ConditionKey;
import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.operations.AbstractLockOperation;
import com.hazelcast.concurrent.lock.operations.AwaitBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class AwaitOperation
extends AbstractLockOperation
implements BlockingOperation,
BackupAwareOperation,
MutatingOperation {
    private String conditionId;
    private boolean expired;

    public AwaitOperation() {
    }

    public AwaitOperation(ObjectNamespace namespace, Data key, long threadId, long timeout, String conditionId) {
        super(namespace, key, threadId, timeout);
        this.conditionId = conditionId;
    }

    public AwaitOperation(ObjectNamespace namespace, Data key, long threadId, long timeout, String conditionId, long referenceId) {
        super(namespace, key, threadId, timeout);
        this.conditionId = conditionId;
        this.setReferenceCallId(referenceId);
    }

    @Override
    public void run() throws Exception {
        LockStoreImpl lockStore = this.getLockStore();
        if (!lockStore.lock(this.key, this.getCallerUuid(), this.threadId, this.getReferenceCallId(), this.leaseTime)) {
            throw new IllegalMonitorStateException("Current thread is not owner of the lock! -> " + lockStore.getOwnerInfo(this.key));
        }
        if (this.expired) {
            this.response = false;
        } else {
            lockStore.removeSignalKey(this.getWaitKey());
            lockStore.removeAwait(this.key, this.conditionId, this.getCallerUuid(), this.threadId);
            this.response = true;
        }
    }

    void runExpired() {
        LockStoreImpl lockStore = this.getLockStore();
        boolean locked = lockStore.lock(this.key, this.getCallerUuid(), this.threadId, this.getReferenceCallId(), this.leaseTime);
        assert (locked) : "Expired await operation should have acquired the lock!";
        this.sendResponse(false);
    }

    @Override
    public ConditionKey getWaitKey() {
        return new ConditionKey(this.namespace.getObjectName(), this.key, this.conditionId, this.getCallerUuid(), this.threadId);
    }

    @Override
    public boolean shouldWait() {
        LockStoreImpl lockStore = this.getLockStore();
        boolean canAcquireLock = lockStore.canAcquireLock(this.key, this.getCallerUuid(), this.threadId);
        if (!canAcquireLock) {
            return true;
        }
        return !lockStore.hasSignalKey(this.getWaitKey());
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return new AwaitBackupOperation(this.namespace, this.key, this.threadId, this.conditionId, this.getCallerUuid());
    }

    @Override
    public void onWaitExpire() {
        this.expired = true;
        LockStoreImpl lockStore = this.getLockStore();
        lockStore.removeSignalKey(this.getWaitKey());
        lockStore.removeAwait(this.key, this.conditionId, this.getCallerUuid(), this.threadId);
        boolean locked = lockStore.lock(this.key, this.getCallerUuid(), this.threadId, this.getReferenceCallId(), this.leaseTime);
        if (locked) {
            this.sendResponse(false);
        } else {
            lockStore.registerExpiredAwaitOp(this);
        }
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.conditionId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.conditionId = in.readUTF();
    }
}

