/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreBackupOperation;

public class AcquireBackupOperation
extends SemaphoreBackupOperation {
    public AcquireBackupOperation() {
    }

    public AcquireBackupOperation(String name, int permitCount, String firstCaller) {
        super(name, permitCount, firstCaller);
    }

    @Override
    public void run() throws Exception {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        semaphoreContainer.acquire(this.firstCaller, this.permitCount);
        this.response = true;
    }

    @Override
    public int getId() {
        return 1;
    }
}

