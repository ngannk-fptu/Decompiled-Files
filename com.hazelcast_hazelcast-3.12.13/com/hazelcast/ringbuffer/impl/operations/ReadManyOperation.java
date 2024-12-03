/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl.operations;

import com.hazelcast.core.IFunction;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.ringbuffer.impl.ReadResultSetImpl;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.operations.AbstractRingBufferOperation;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public class ReadManyOperation<O>
extends AbstractRingBufferOperation
implements BlockingOperation,
ReadonlyOperation,
Versioned {
    transient long sequence;
    private int minSize;
    private int maxSize;
    private long startSequence;
    private IFunction<O, Boolean> filter;
    private transient ReadResultSetImpl<O, O> resultSet;

    public ReadManyOperation() {
    }

    public ReadManyOperation(String name, long startSequence, int minSize, int maxSize, IFunction<O, Boolean> filter) {
        super(name);
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.startSequence = startSequence;
        this.filter = filter;
    }

    @Override
    public void beforeRun() {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        ringbuffer.checkBlockableReadSequence(this.startSequence);
    }

    @Override
    public boolean shouldWait() {
        if (this.resultSet == null) {
            this.resultSet = new ReadResultSetImpl(this.minSize, this.maxSize, this.getNodeEngine().getSerializationService(), this.filter);
            this.sequence = this.startSequence;
        }
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        if (this.minSize == 0) {
            if (!ringbuffer.shouldWait(this.sequence)) {
                this.sequence = ringbuffer.readMany(this.sequence, this.resultSet);
            }
            return false;
        }
        if (this.resultSet.isMinSizeReached()) {
            return false;
        }
        if (ringbuffer.isTooLargeSequence(this.sequence) || ringbuffer.isStaleSequence(this.sequence)) {
            return false;
        }
        if (this.sequence == ringbuffer.tailSequence() + 1L) {
            return true;
        }
        this.sequence = ringbuffer.readMany(this.sequence, this.resultSet);
        return !this.resultSet.isMinSizeReached();
    }

    @Override
    public void run() throws Exception {
    }

    @Override
    public Object getResponse() {
        return this.resultSet;
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
    public int getId() {
        return 6;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.startSequence);
        out.writeInt(this.minSize);
        out.writeInt(this.maxSize);
        out.writeObject(this.filter);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.startSequence = in.readLong();
        this.minSize = in.readInt();
        this.maxSize = in.readInt();
        this.filter = (IFunction)in.readObject();
    }
}

