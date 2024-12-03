/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.concurrent.atomicreference.operations.AbstractAtomicReferenceOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class IsNullOperation
extends AbstractAtomicReferenceOperation
implements ReadonlyOperation {
    private boolean returnValue;

    public IsNullOperation() {
    }

    public IsNullOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        AtomicReferenceContainer container = this.getReferenceContainer();
        this.returnValue = container.isNull();
    }

    @Override
    public Object getResponse() {
        return this.returnValue;
    }

    @Override
    public int getId() {
        return 8;
    }
}

