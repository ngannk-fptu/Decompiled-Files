/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.operations.AbstractLockOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ObjectNamespace;
import java.io.IOException;

abstract class BaseSignalOperation
extends AbstractLockOperation {
    protected boolean all;
    protected String conditionId;
    protected transient int awaitCount;

    public BaseSignalOperation() {
    }

    public BaseSignalOperation(ObjectNamespace namespace, Data key, long threadId, String conditionId, boolean all) {
        super(namespace, key, threadId);
        this.conditionId = conditionId;
        this.all = all;
    }

    @Override
    public void run() throws Exception {
        this.response = true;
        LockStoreImpl lockStore = this.getLockStore();
        int signalCount = this.all ? Integer.MAX_VALUE : 1;
        lockStore.signal(this.key, this.conditionId, signalCount, this.namespace.getObjectName());
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.all);
        out.writeUTF(this.conditionId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.all = in.readBoolean();
        this.conditionId = in.readUTF();
    }
}

