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

public class LockBackupOperation
extends AbstractLockOperation
implements BackupOperation {
    private String originalCallerUuid;

    public LockBackupOperation() {
    }

    public LockBackupOperation(ObjectNamespace namespace, Data key, long threadId, long leaseTime, String originalCallerUuid) {
        super(namespace, key, threadId);
        this.leaseTime = leaseTime;
        this.originalCallerUuid = originalCallerUuid;
    }

    @Override
    public void run() throws Exception {
        this.interceptLockOperation();
        LockStoreImpl lockStore = this.getLockStore();
        this.response = lockStore.lock(this.key, this.originalCallerUuid, this.threadId, this.getReferenceCallId(), this.leaseTime);
    }

    @Override
    public int getId() {
        return 10;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.originalCallerUuid);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.originalCallerUuid = in.readUTF();
    }
}

