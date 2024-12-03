/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.operations.AbstractAlterOperation;
import com.hazelcast.core.IFunction;

public class AlterAndGetOperation
extends AbstractAlterOperation {
    public AlterAndGetOperation() {
    }

    public AlterAndGetOperation(String name, IFunction<Long, Long> function) {
        super(name, function);
    }

    @Override
    public void run() throws Exception {
        long output;
        AtomicLongContainer container = this.getLongContainer();
        long input = container.get();
        boolean bl = this.shouldBackup = input != (output = ((Long)this.function.apply(input)).longValue());
        if (this.shouldBackup) {
            this.backup = output;
            container.set(output);
        }
        this.response = output;
    }

    @Override
    public int getId() {
        return 3;
    }
}

