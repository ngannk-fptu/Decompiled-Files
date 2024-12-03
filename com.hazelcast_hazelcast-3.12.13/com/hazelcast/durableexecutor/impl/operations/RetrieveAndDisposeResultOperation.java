/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.durableexecutor.impl.DurableExecutorContainer;
import com.hazelcast.durableexecutor.impl.operations.DisposeResultOperation;
import com.hazelcast.durableexecutor.impl.operations.DurableExecutorWaitNotifyKey;
import com.hazelcast.nio.Bits;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;

public class RetrieveAndDisposeResultOperation
extends DisposeResultOperation
implements BlockingOperation,
MutatingOperation {
    private transient Object result;

    public RetrieveAndDisposeResultOperation() {
    }

    public RetrieveAndDisposeResultOperation(String name, int sequence) {
        super(name, sequence);
    }

    @Override
    public void run() throws Exception {
        DurableExecutorContainer executorContainer = this.getExecutorContainer();
        this.result = executorContainer.retrieveAndDisposeResult(this.sequence);
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        long uniqueId = Bits.combineToLong(this.getPartitionId(), this.sequence);
        return new DurableExecutorWaitNotifyKey(this.name, uniqueId);
    }

    @Override
    public boolean shouldWait() {
        DurableExecutorContainer executorContainer = this.getExecutorContainer();
        return executorContainer.shouldWait(this.sequence);
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(new HazelcastException());
    }

    @Override
    public int getId() {
        return 4;
    }
}

