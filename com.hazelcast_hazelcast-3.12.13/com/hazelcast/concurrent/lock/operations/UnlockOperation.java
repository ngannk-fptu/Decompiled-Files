/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.operations.AbstractLockOperation;
import com.hazelcast.concurrent.lock.operations.AwaitOperation;
import com.hazelcast.concurrent.lock.operations.UnlockBackupOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class UnlockOperation
extends AbstractLockOperation
implements Notifier,
BackupAwareOperation,
MutatingOperation {
    private boolean force;
    private boolean shouldNotify;

    public UnlockOperation() {
    }

    public UnlockOperation(ObjectNamespace namespace, Data key, long threadId) {
        super(namespace, key, threadId);
    }

    public UnlockOperation(ObjectNamespace namespace, Data key, long threadId, boolean force) {
        super(namespace, key, threadId);
        this.force = force;
    }

    public UnlockOperation(ObjectNamespace namespace, Data key, long threadId, boolean force, long referenceId) {
        super(namespace, key, threadId);
        this.force = force;
        this.setReferenceCallId(referenceId);
    }

    @Override
    public void run() throws Exception {
        if (this.force) {
            this.forceUnlock();
        } else {
            this.unlock();
        }
    }

    protected final void unlock() {
        LockStoreImpl lockStore = this.getLockStore();
        boolean unlocked = lockStore.unlock(this.key, this.getCallerUuid(), this.threadId, this.getReferenceCallId());
        this.response = unlocked;
        if (!unlocked) {
            String ownerInfo = lockStore.getOwnerInfo(this.key);
            throw new IllegalMonitorStateException("Current thread is not owner of the lock! -> " + ownerInfo);
        }
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            logger.finest("Released lock " + this.namespace.getObjectName());
        }
    }

    protected final void forceUnlock() {
        LockStoreImpl lockStore = this.getLockStore();
        boolean unlocked = lockStore.forceUnlock(this.key);
        this.response = unlocked;
        ILogger logger = this.getLogger();
        if (logger.isFinestEnabled()) {
            if (unlocked) {
                logger.finest("Released lock " + this.namespace.getObjectName());
            } else {
                logger.finest("Could not release lock " + this.namespace.getObjectName() + " as it is not locked");
            }
        }
    }

    @Override
    public void afterRun() throws Exception {
        LockStoreImpl lockStore = this.getLockStore();
        AwaitOperation awaitOperation = lockStore.pollExpiredAwaitOp(this.key);
        if (awaitOperation != null) {
            awaitOperation.runExpired();
        }
        this.shouldNotify = awaitOperation == null;
    }

    @Override
    public Operation getBackupOperation() {
        UnlockBackupOperation operation = new UnlockBackupOperation(this.namespace, this.key, this.threadId, this.getCallerUuid(), this.force);
        operation.setReferenceCallId(this.getReferenceCallId());
        return operation;
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public boolean shouldNotify() {
        return this.shouldNotify;
    }

    @Override
    public final WaitNotifyKey getNotifiedKey() {
        LockStoreImpl lockStore = this.getLockStore();
        return lockStore.getNotifiedKey(this.key);
    }

    @Override
    public int getId() {
        return 16;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.force);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.force = in.readBoolean();
    }
}

