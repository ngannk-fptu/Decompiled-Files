/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.operations.AbstractBackupAwareMultiMapOperation;
import com.hazelcast.multimap.impl.operations.DeleteBackupOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;

public class DeleteOperation
extends AbstractBackupAwareMultiMapOperation {
    private transient boolean shouldBackup;

    public DeleteOperation() {
    }

    public DeleteOperation(String name, Data dataKey, long threadId) {
        super(name, dataKey, threadId);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        if (container.delete(this.dataKey)) {
            container.update();
        }
        this.shouldBackup = true;
    }

    @Override
    public Operation getBackupOperation() {
        return new DeleteBackupOperation(this.name, this.dataKey);
    }

    @Override
    public boolean shouldWait() {
        MultiMapContainer container = this.getOrCreateContainer();
        if (container.isTransactionallyLocked(this.dataKey)) {
            return !container.canAcquireLock(this.dataKey, this.getCallerUuid(), this.threadId);
        }
        return false;
    }

    @Override
    public boolean shouldBackup() {
        return this.shouldBackup;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    public int getId() {
        return 51;
    }
}

