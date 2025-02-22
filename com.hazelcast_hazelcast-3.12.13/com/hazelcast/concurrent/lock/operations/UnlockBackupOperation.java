/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.operations.AbstractLockOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.ObjectNamespace;
import java.io.IOException;

public class UnlockBackupOperation
extends AbstractLockOperation
implements BackupOperation {
    private boolean force;
    private String originalCallerUuid;

    public UnlockBackupOperation() {
    }

    public UnlockBackupOperation(ObjectNamespace namespace, Data key, long threadId, String originalCallerUuid, boolean force) {
        super(namespace, key, threadId);
        this.force = force;
        this.originalCallerUuid = originalCallerUuid;
    }

    @Override
    public void run() throws Exception {
        LockStoreImpl lockStore = this.getLockStore();
        boolean unlocked = this.force ? lockStore.forceUnlock(this.key) : lockStore.unlock(this.key, this.originalCallerUuid, this.threadId, this.getReferenceCallId());
        this.response = unlocked;
        lockStore.pollExpiredAwaitOp(this.key);
    }

    @Override
    public int getId() {
        return 15;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.originalCallerUuid);
        out.writeBoolean(this.force);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.originalCallerUuid = in.readUTF();
        this.force = in.readBoolean();
    }
}

