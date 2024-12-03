/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.SemaphoreWaitNotifyKey;
import com.hazelcast.concurrent.semaphore.operations.ReleaseBackupOperation;
import com.hazelcast.concurrent.semaphore.operations.SemaphoreBackupAwareOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;

public class ReleaseOperation
extends SemaphoreBackupAwareOperation
implements Notifier,
MutatingOperation {
    public ReleaseOperation() {
    }

    public ReleaseOperation(String name, int permitCount) {
        super(name, permitCount);
    }

    @Override
    public void run() throws Exception {
        SemaphoreContainer semaphoreContainer = this.getSemaphoreContainer();
        semaphoreContainer.release(this.getCallerUuid(), this.permitCount);
        this.response = true;
    }

    @Override
    public boolean shouldNotify() {
        return this.permitCount > 0;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return new SemaphoreWaitNotifyKey(this.name, "acquire");
    }

    @Override
    public boolean shouldBackup() {
        return this.permitCount > 0;
    }

    @Override
    public Operation getBackupOperation() {
        return new ReleaseBackupOperation(this.name, this.permitCount, this.getCallerUuid());
    }

    @Override
    public int getId() {
        return 12;
    }
}

