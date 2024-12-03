/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.operations.AbstractRingBufferOperation;
import com.hazelcast.ringbuffer.impl.operations.AddBackupOperation;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class AddOperation
extends AbstractRingBufferOperation
implements Notifier,
BackupAwareOperation,
MutatingOperation {
    private Data item;
    private long resultSequence;
    private OverflowPolicy overflowPolicy;

    public AddOperation() {
    }

    public AddOperation(String name, Data item, OverflowPolicy overflowPolicy) {
        super(name);
        this.item = item;
        this.overflowPolicy = overflowPolicy;
    }

    @Override
    public void run() throws Exception {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        if (this.overflowPolicy == OverflowPolicy.FAIL && ringbuffer.remainingCapacity() < 1L) {
            this.resultSequence = -1L;
            return;
        }
        this.resultSequence = ringbuffer.add(this.item);
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        return ringbuffer.getRingEmptyWaitNotifyKey();
    }

    @Override
    public boolean shouldNotify() {
        return this.resultSequence != -1L;
    }

    @Override
    public boolean shouldBackup() {
        return this.resultSequence != -1L;
    }

    @Override
    public int getSyncBackupCount() {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        return ringbuffer.getConfig().getBackupCount();
    }

    @Override
    public int getAsyncBackupCount() {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        return ringbuffer.getConfig().getAsyncBackupCount();
    }

    @Override
    public Operation getBackupOperation() {
        return new AddBackupOperation(this.name, this.resultSequence, this.item);
    }

    @Override
    public Long getResponse() {
        return this.resultSequence;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.item);
        out.writeInt(this.overflowPolicy.getId());
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.item = in.readData();
        this.overflowPolicy = OverflowPolicy.getById(in.readInt());
    }
}

