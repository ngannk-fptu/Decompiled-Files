/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.durableexecutor.impl.DurableExecutorContainer;
import com.hazelcast.durableexecutor.impl.operations.AbstractDurableExecutorOperation;
import com.hazelcast.durableexecutor.impl.operations.DurableExecutorWaitNotifyKey;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public class RetrieveResultOperation
extends AbstractDurableExecutorOperation
implements BlockingOperation,
ReadonlyOperation {
    private int sequence;
    private transient Object result;

    public RetrieveResultOperation() {
    }

    public RetrieveResultOperation(String name, int sequence) {
        super(name);
        this.sequence = sequence;
    }

    @Override
    public void run() throws Exception {
        DurableExecutorContainer executorContainer = this.getExecutorContainer();
        this.result = executorContainer.retrieveResult(this.sequence);
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
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.sequence);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.sequence = in.readInt();
    }

    @Override
    public int getId() {
        return 5;
    }
}

