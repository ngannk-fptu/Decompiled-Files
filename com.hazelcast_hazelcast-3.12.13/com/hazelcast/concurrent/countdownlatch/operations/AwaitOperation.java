/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch.operations;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchService;
import com.hazelcast.concurrent.countdownlatch.operations.AbstractCountDownLatchOperation;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;

public class AwaitOperation
extends AbstractCountDownLatchOperation
implements BlockingOperation,
ReadonlyOperation {
    public AwaitOperation() {
    }

    public AwaitOperation(String name, long timeout) {
        super(name);
        this.setWaitTimeout(timeout);
    }

    @Override
    public void run() throws Exception {
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return this.waitNotifyKey();
    }

    @Override
    public boolean shouldWait() {
        CountDownLatchService service = (CountDownLatchService)this.getService();
        return service.shouldWait(this.name);
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    public int getId() {
        return 1;
    }
}

