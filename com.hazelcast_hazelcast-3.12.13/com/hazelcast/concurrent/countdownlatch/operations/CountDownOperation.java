/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch.operations;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchService;
import com.hazelcast.concurrent.countdownlatch.operations.BackupAwareCountDownLatchOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;

public class CountDownOperation
extends BackupAwareCountDownLatchOperation
implements Notifier,
MutatingOperation {
    private boolean shouldNotify;

    public CountDownOperation() {
    }

    public CountDownOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        CountDownLatchService service = (CountDownLatchService)this.getService();
        service.countDown(this.name);
        int count = service.getCount(this.name);
        this.shouldNotify = count == 0;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public boolean shouldNotify() {
        return this.shouldNotify;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return this.waitNotifyKey();
    }

    @Override
    public int getId() {
        return 4;
    }
}

