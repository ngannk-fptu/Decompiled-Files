/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.operations.AbstractRingBufferOperation;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public class ReadOneOperation
extends AbstractRingBufferOperation
implements BlockingOperation,
ReadonlyOperation {
    private long sequence;
    private Data result;

    public ReadOneOperation() {
    }

    public ReadOneOperation(String name, long sequence) {
        super(name);
        this.sequence = sequence;
    }

    @Override
    public void beforeRun() throws Exception {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        ringbuffer.checkBlockableReadSequence(this.sequence);
    }

    @Override
    public boolean shouldWait() {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        if (ringbuffer.isTooLargeSequence(this.sequence) || ringbuffer.isStaleSequence(this.sequence)) {
            return false;
        }
        return this.sequence == ringbuffer.tailSequence() + 1L;
    }

    @Override
    public void run() throws Exception {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        this.result = ringbuffer.readAsData(this.sequence);
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        return ringbuffer.getRingEmptyWaitNotifyKey();
    }

    @Override
    public void onWaitExpire() {
    }

    @Override
    public Data getResponse() {
        return this.result;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.sequence);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.sequence = in.readLong();
    }
}

