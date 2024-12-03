/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreBackupOperation;

public class DrainBackupOperation
extends SemaphoreBackupOperation {
    public DrainBackupOperation() {
    }

    public DrainBackupOperation(String name, int permitCount, String firstCaller) {
        super(name, permitCount, firstCaller);
    }

    @Override
    public void run() throws Exception {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        semaphoreContainer.drain(this.firstCaller);
        this.response = true;
    }

    @Override
    public int getId() {
        return 5;
    }
}

