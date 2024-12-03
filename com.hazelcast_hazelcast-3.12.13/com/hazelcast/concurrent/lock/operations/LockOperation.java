/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.concurrent.lock.operations.AbstractLockOperation;
import com.hazelcast.concurrent.lock.operations.LockBackupOperation;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.Serializable;

public class LockOperation
extends AbstractLockOperation
implements BlockingOperation,
BackupAwareOperation,
MutatingOperation {
    public LockOperation() {
    }

    public LockOperation(ObjectNamespace namespace, Data key, long threadId, long leaseTime, long timeout) {
        super(namespace, key, threadId, leaseTime, timeout);
    }

    public LockOperation(ObjectNamespace namespace, Data key, long threadId, long leaseTime, long timeout, long referenceId) {
        super(namespace, key, threadId, leaseTime, timeout);
        this.setReferenceCallId(referenceId);
    }

    @Override
    public void run() throws Exception {
        this.interceptLockOperation();
        boolean lockResult = this.getLockStore().lock(this.key, this.getCallerUuid(), this.threadId, this.getReferenceCallId(), this.leaseTime);
        this.response = lockResult;
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            if (lockResult) {
                logger.finest("Acquired lock " + this.namespace.getObjectName() + " for " + this.getCallerAddress() + " - " + this.getCallerUuid() + ", thread ID: " + this.threadId);
            } else {
                logger.finest("Could not acquire lock " + this.namespace.getObjectName() + " as owned by " + this.getLockStore().getOwnerInfo(this.key));
            }
        }
    }

    @Override
    public Operation getBackupOperation() {
        LockBackupOperation operation = new LockBackupOperation(this.namespace, this.key, this.threadId, this.leaseTime, this.getCallerUuid());
        operation.setReferenceCallId(this.getReferenceCallId());
        return operation;
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public final WaitNotifyKey getWaitKey() {
        return new LockWaitNotifyKey(this.namespace, this.key);
    }

    @Override
    public final boolean shouldWait() {
        LockStoreImpl lockStore = this.getLockStore();
        return this.getWaitTimeout() != 0L && !lockStore.canAcquireLock(this.key, this.getCallerUuid(), this.threadId);
    }

    @Override
    public final void onWaitExpire() {
        long timeout = this.getWaitTimeout();
        Serializable response = timeout < 0L || timeout == Long.MAX_VALUE ? new OperationTimeoutException() : Boolean.FALSE;
        this.sendResponse(response);
    }

    @Override
    public int getId() {
        return 11;
    }
}

