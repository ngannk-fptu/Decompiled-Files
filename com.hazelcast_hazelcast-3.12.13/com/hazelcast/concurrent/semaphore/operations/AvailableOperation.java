/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class AvailableOperation
extends SemaphoreOperation
implements ReadonlyOperation {
    public AvailableOperation() {
    }

    public AvailableOperation(String name) {
        super(name, -1);
    }

    @Override
    public void run() throws Exception {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        this.response = semaphoreContainer.getAvailable();
    }

    @Override
    public int getId() {
        return 3;
    }
}

