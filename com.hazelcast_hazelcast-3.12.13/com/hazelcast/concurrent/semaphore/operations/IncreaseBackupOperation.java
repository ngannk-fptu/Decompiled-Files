/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreBackupOperation;

public class IncreaseBackupOperation
extends SemaphoreBackupOperation {
    public IncreaseBackupOperation() {
    }

    public IncreaseBackupOperation(String name, int permitCount) {
        super(name, permitCount, null);
    }

    @Override
    public void run() throws Exception {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        semaphoreContainer.increase(this.permitCount);
        this.response = true;
    }

    @Override
    public int getId() {
        return 16;
    }
}

