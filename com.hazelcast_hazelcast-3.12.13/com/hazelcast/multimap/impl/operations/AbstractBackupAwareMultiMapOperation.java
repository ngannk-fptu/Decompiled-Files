/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public abstract class AbstractBackupAwareMultiMapOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupAwareOperation,
BlockingOperation {
    protected AbstractBackupAwareMultiMapOperation() {
    }

    protected AbstractBackupAwareMultiMapOperation(String name, Data dataKey, long threadId) {
        super(name, dataKey, threadId);
    }

    @Override
    public boolean shouldBackup() {
        return this.response != null;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return new LockWaitNotifyKey(new DistributedObjectNamespace("hz:impl:multiMapService", this.name), this.dataKey);
    }

    @Override
    public boolean shouldWait() {
        return !this.getOrCreateContainer().canAcquireLock(this.dataKey, this.getCallerUuid(), this.threadId);
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(null);
    }
}

