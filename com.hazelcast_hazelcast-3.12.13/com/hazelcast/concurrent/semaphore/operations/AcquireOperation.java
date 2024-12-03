/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.SemaphoreWaitNotifyKey;
import com.hazelcast.concurrent.semaphore.operations.AcquireBackupOperation;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreBackupAwareOperation;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;

public class AcquireOperation
extends SemaphoreBackupAwareOperation
implements BlockingOperation,
MutatingOperation {
    public AcquireOperation() {
    }

    public AcquireOperation(String name, int permitCount, long timeout) {
        super(name, permitCount);
        this.setWaitTimeout(timeout);
    }

    @Override
    public void run() throws Exception {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        this.response = semaphoreContainer.acquire(this.getCallerUuid(), this.permitCount);
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return new SemaphoreWaitNotifyKey(this.name, "acquire");
    }

    @Override
    public boolean shouldWait() {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        return this.getWaitTimeout() != 0L && !semaphoreContainer.isAvailable(this.permitCount);
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new AcquireBackupOperation(this.name, this.permitCount, this.getCallerUuid());
    }

    @Override
    public int getId() {
        return 2;
    }
}

