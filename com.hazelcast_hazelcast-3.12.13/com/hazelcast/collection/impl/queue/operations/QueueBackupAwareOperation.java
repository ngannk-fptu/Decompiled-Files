/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.spi.BackupAwareOperation;

public abstract class QueueBackupAwareOperation
extends QueueOperation
implements BackupAwareOperation {
    protected QueueBackupAwareOperation() {
    }

    protected QueueBackupAwareOperation(String name) {
        super(name);
    }

    protected QueueBackupAwareOperation(String name, long timeoutMillis) {
        super(name, timeoutMillis);
    }

    @Override
    public final int getSyncBackupCount() {
        return this.getContainer().getConfig().getBackupCount();
    }

    @Override
    public final int getAsyncBackupCount() {
        return this.getContainer().getConfig().getAsyncBackupCount();
    }
}

