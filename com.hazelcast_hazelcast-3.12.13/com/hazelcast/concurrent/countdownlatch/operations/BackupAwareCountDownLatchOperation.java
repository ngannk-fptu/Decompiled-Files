/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch.operations;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchContainer;
import com.hazelcast.concurrent.countdownlatch.CountDownLatchService;
import com.hazelcast.concurrent.countdownlatch.operations.AbstractCountDownLatchOperation;
import com.hazelcast.concurrent.countdownlatch.operations.CountDownLatchBackupOperation;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;

abstract class BackupAwareCountDownLatchOperation
extends AbstractCountDownLatchOperation
implements BackupAwareOperation {
    protected BackupAwareCountDownLatchOperation() {
    }

    public BackupAwareCountDownLatchOperation(String name) {
        super(name);
    }

    @Override
    public Operation getBackupOperation() {
        CountDownLatchService service = (CountDownLatchService)this.getService();
        CountDownLatchContainer container = service.getCountDownLatchContainer(this.name);
        int count = container != null ? container.getCount() : 0;
        return new CountDownLatchBackupOperation(this.name, count);
    }

    @Override
    public int getSyncBackupCount() {
        return 1;
    }

    @Override
    public int getAsyncBackupCount() {
        return 0;
    }
}

