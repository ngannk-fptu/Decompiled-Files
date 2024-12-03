/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.operations.DrainBackupOperation;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreBackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;

public class DrainOperation
extends SemaphoreBackupAwareOperation
implements MutatingOperation {
    public DrainOperation() {
    }

    public DrainOperation(String name) {
        super(name, -1);
    }

    @Override
    public void run() throws Exception {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        this.response = semaphoreContainer.drain(this.getCallerUuid());
    }

    @Override
    public boolean shouldBackup() {
        return !this.response.equals(0);
    }

    @Override
    public Operation getBackupOperation() {
        return new DrainBackupOperation(this.name, this.permitCount, this.getCallerUuid());
    }

    @Override
    public int getId() {
        return 6;
    }
}

