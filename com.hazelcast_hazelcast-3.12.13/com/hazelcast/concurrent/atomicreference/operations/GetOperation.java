/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.operations.AbstractAtomicReferenceOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ReadonlyOperation;

public class GetOperation
extends AbstractAtomicReferenceOperation
implements ReadonlyOperation {
    private Data returnValue;

    public GetOperation() {
    }

    public GetOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        this.returnValue = this.getReferenceContainer().get();
    }

    @Override
    public Object getResponse() {
        return this.returnValue;
    }

    @Override
    public int getId() {
        return 7;
    }
}

