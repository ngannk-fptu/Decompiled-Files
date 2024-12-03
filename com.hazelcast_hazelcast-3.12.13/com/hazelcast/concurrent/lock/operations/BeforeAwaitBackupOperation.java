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

public class BeforeAwaitBackupOperation
extends AbstractLockOperation
implements BackupOperation {
    private String conditionId;
    private String originalCaller;

    public BeforeAwaitBackupOperation() {
    }

    public BeforeAwaitBackupOperation(ObjectNamespace namespace, Data key, long threadId, String conditionId, String originalCaller) {
        super(namespace, key, threadId);
        this.conditionId = conditionId;
        this.originalCaller = originalCaller;
    }

    @Override
    public void run() throws Exception {
        LockStoreImpl lockStore = this.getLockStore();
        lockStore.addAwait(this.key, this.conditionId, this.originalCaller, this.threadId);
        lockStore.unlock(this.key, this.originalCaller, this.threadId, this.getReferenceCallId());
        this.response = true;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.originalCaller);
        out.writeUTF(this.conditionId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.originalCaller = in.readUTF();
        this.conditionId = in.readUTF();
    }
}

