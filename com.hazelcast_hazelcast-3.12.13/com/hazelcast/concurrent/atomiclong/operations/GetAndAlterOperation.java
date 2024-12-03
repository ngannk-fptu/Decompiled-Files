/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.operations.AbstractAlterOperation;
import com.hazelcast.core.IFunction;

public class GetAndAlterOperation
extends AbstractAlterOperation {
    public GetAndAlterOperation() {
    }

    public GetAndAlterOperation(String name, IFunction<Long, Long> function) {
        super(name, function);
    }

    @Override
    public void run() throws Exception {
        long input;
        AtomicLongContainer container = this.getLongContainer();
        this.response = input = container.get();
        long output = (Long)this.function.apply(input);
        boolean bl = this.shouldBackup = input != output;
        if (this.shouldBackup) {
            this.backup = output;
            container.set(output);
        }
    }

    @Override
    public int getId() {
        return 8;
    }
}

