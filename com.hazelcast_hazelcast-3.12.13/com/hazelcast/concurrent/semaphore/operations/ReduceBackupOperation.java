/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreBackupOperation;

public class ReduceBackupOperation
extends SemaphoreBackupOperation {
    public ReduceBackupOperation() {
    }

    public ReduceBackupOperation(String name, int permitCount) {
        super(name, permitCount, null);
    }

    @Override
    public void run() throws Exception {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        semaphoreContainer.reduce(this.permitCount);
        this.response = true;
    }

    @Override
    public int getId() {
        return 9;
    }
}

