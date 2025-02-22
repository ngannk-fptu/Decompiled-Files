/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.ConditionKey;
import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.operations.AbstractLockOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.ObjectNamespace;
import java.io.IOException;

public class AwaitBackupOperation
extends AbstractLockOperation
implements BackupOperation {
    private String originalCaller;
    private String conditionId;

    public AwaitBackupOperation() {
    }

    public AwaitBackupOperation(ObjectNamespace namespace, Data key, long threadId, String conditionId, String originalCaller) {
        super(namespace, key, threadId);
        this.conditionId = conditionId;
        this.originalCaller = originalCaller;
    }

    @Override
    public void run() throws Exception {
        LockStoreImpl lockStore = this.getLockStore();
        lockStore.lock(this.key, this.originalCaller, this.threadId, this.getReferenceCallId(), this.leaseTime);
        ConditionKey conditionKey = new ConditionKey(this.namespace.getObjectName(), this.key, this.conditionId, this.originalCaller, this.threadId);
        lockStore.removeSignalKey(conditionKey);
        lockStore.removeAwait(this.key, this.conditionId, this.originalCaller, this.threadId);
        this.response = true;
    }

    @Override
    public int getId() {
        return 3;
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

