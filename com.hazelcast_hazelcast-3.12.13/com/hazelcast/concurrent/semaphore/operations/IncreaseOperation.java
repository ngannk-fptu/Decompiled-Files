/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.operations.IncreaseBackupOperation;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreBackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;

public class IncreaseOperation
extends SemaphoreBackupAwareOperation
implements MutatingOperation {
    public IncreaseOperation() {
    }

    public IncreaseOperation(String name, int permitCount) {
        super(name, permitCount);
    }

    @Override
    public void run() throws Exception {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        this.response = semaphoreContainer.increase(this.permitCount);
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new IncreaseBackupOperation(this.name, this.permitCount);
    }

    @Override
    public int getId() {
        return 15;
    }
}

