/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch.operations;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchService;
import com.hazelcast.concurrent.countdownlatch.operations.AbstractCountDownLatchOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class GetCountOperation
extends AbstractCountDownLatchOperation
implements ReadonlyOperation {
    private int count;

    public GetCountOperation() {
    }

    public GetCountOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        CountDownLatchService service = (CountDownLatchService)this.getService();
        this.count = service.getCount(this.name);
    }

    @Override
    public Object getResponse() {
        return this.count;
    }

    @Override
    public int getId() {
        return 5;
    }
}

