/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.ringbuffer.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.operations.AbstractRingBufferOperation;
import com.hazelcast.ringbuffer.impl.operations.AddAllBackupOperation;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public class AddAllOperation
extends AbstractRingBufferOperation
implements Notifier,
BackupAwareOperation,
MutatingOperation {
    private OverflowPolicy overflowPolicy;
    private Data[] items;
    private long lastSequence;

    public AddAllOperation() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public AddAllOperation(String name, Data[] items, OverflowPolicy overflowPolicy) {
        super(name);
        this.items = items;
        this.overflowPolicy = overflowPolicy;
    }

    @Override
    public void run() throws Exception {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        if (this.overflowPolicy == OverflowPolicy.FAIL && ringbuffer.remainingCapacity() < (long)this.items.length) {
            this.lastSequence = -1L;
            return;
        }
        this.lastSequence = ringbuffer.addAll(this.items);
    }

    @Override
    public Object getResponse() {
        return this.lastSequence;
    }

    @Override
    public boolean shouldNotify() {
        return this.lastSequence != -1L;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        return ringbuffer.getRingEmptyWaitNotifyKey();
    }

    @Override
    public boolean shouldBackup() {
        return this.lastSequence != -1L;
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
        return new AddAllBackupOperation(this.name, this.lastSequence, this.items);
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.overflowPolicy.getId());
        out.writeInt(this.items.length);
        for (Data item : this.items) {
            out.writeData(item);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.overflowPolicy = OverflowPolicy.getById(in.readInt());
        int length = in.readInt();
        this.items = new Data[length];
        for (int k = 0; k < this.items.length; ++k) {
            this.items[k] = in.readData();
        }
    }
}

