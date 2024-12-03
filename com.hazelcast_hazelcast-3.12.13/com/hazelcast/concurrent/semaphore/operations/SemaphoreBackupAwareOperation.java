/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreOperation;
import com.hazelcast.spi.BackupAwareOperation;

public abstract class SemaphoreBackupAwareOperation
extends SemaphoreOperation
implements BackupAwareOperation {
    protected SemaphoreBackupAwareOperation() {
    }

    protected SemaphoreBackupAwareOperation(String name, int permitCount) {
        super(name, permitCount);
    }

    @Override
    public int getAsyncBackupCount() {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        return semaphoreContainer.getAsyncBackupCount();
    }

    @Override
    public int getSyncBackupCount() {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        return semaphoreContainer.getSyncBackupCount();
    }
}

