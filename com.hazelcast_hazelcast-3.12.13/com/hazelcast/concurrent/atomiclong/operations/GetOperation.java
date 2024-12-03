/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.operations.AbstractAtomicLongOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class GetOperation
extends AbstractAtomicLongOperation
implements ReadonlyOperation {
    private long returnValue;

    public GetOperation() {
    }

    public GetOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        AtomicLongContainer container = this.getLongContainer();
        this.returnValue = container.get();
    }

    @Override
    public Object getResponse() {
        return this.returnValue;
    }

    @Override
    public int getId() {
        return 6;
    }
}

