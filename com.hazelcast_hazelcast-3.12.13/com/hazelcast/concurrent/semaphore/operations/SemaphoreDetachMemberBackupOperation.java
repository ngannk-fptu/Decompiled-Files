/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.SemaphoreService;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreBackupOperation;

public class SemaphoreDetachMemberBackupOperation
extends SemaphoreBackupOperation {
    public SemaphoreDetachMemberBackupOperation() {
    }

    public SemaphoreDetachMemberBackupOperation(String name, String firstCaller) {
        super(name, -1, firstCaller);
    }

    @Override
    public void run() throws Exception {
        SemaphoreService service = (SemaphoreService)this.getService();
        if (service.containsSemaphore(this.name)) {
            SemaphoreContainer semaphoreContainer = service.getSemaphoreContainer(this.name);
            this.response = semaphoreContainer.detachAll(this.firstCaller);
        }
    }

    @Override
    public int getId() {
        return 4;
    }
}

