/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.operations.AbstractLockOperation;
import com.hazelcast.concurrent.lock.operations.BeforeAwaitBackupOperation;
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

public class BeforeAwaitOperation
extends AbstractLockOperation
implements Notifier,
BackupAwareOperation,
MutatingOperation {
    private String conditionId;

    public BeforeAwaitOperation() {
    }

    public BeforeAwaitOperation(ObjectNamespace namespace, Data key, long threadId, String conditionId) {
        super(namespace, key, threadId);
        this.conditionId = conditionId;
    }

    public BeforeAwaitOperation(ObjectNamespace namespace, Data key, long threadId, String conditionId, long referenceId) {
        super(namespace, key, threadId);
        this.conditionId = conditionId;
        this.setReferenceCallId(referenceId);
    }

    @Override
    public void beforeRun() throws Exception {
        LockStoreImpl lockStore = this.getLockStore();
        boolean isLockOwner = lockStore.isLockedBy(this.key, this.getCallerUuid(), this.threadId);
        this.ensureOwner(lockStore, isLockOwner);
    }

    private void ensureOwner(LockStoreImpl lockStore, boolean isLockOwner) {
        if (!isLockOwner) {
            throw new IllegalMonitorStateException("Current thread is not owner of the lock! -> " + lockStore.getOwnerInfo(this.key));
        }
    }

    @Override
    public void run() throws Exception {
        LockStoreImpl lockStore = this.getLockStore();
        lockStore.addAwait(this.key, this.conditionId, this.getCallerUuid(), this.threadId);
        lockStore.unlock(this.key, this.getCallerUuid(), this.threadId, this.getReferenceCallId());
    }

    @Override
    public boolean shouldNotify() {
        return true;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return new BeforeAwaitBackupOperation(this.namespace, this.key, this.threadId, this.conditionId, this.getCallerUuid());
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        LockStoreImpl lockStore = this.getLockStore();
        return lockStore.getNotifiedKey(this.key);
    }

    @Override
    public int getId() {
        return 6;
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

