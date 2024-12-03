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
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.operations.AbstractRingBufferOperation;
import com.hazelcast.spi.BackupOperation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public class AddAllBackupOperation
extends AbstractRingBufferOperation
implements BackupOperation {
    private long lastSequenceId;
    private Data[] items;

    public AddAllBackupOperation() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public AddAllBackupOperation(String name, long lastSequenceId, Data[] items) {
        super(name);
        this.items = items;
        this.lastSequenceId = lastSequenceId;
    }

    @Override
    public void run() throws Exception {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        long firstSequenceId = this.lastSequenceId - (long)this.items.length + 1L;
        for (int i = 0; i < this.items.length; ++i) {
            ringbuffer.set(firstSequenceId + (long)i, this.items[i]);
        }
    }

    @Override
    public int getId() {
        return 8;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.lastSequenceId);
        out.writeInt(this.items.length);
        for (Data item : this.items) {
            out.writeData(item);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.lastSequenceId = in.readLong();
        int length = in.readInt();
        this.items = new Data[length];
        for (int k = 0; k < this.items.length; ++k) {
            this.items[k] = in.readData();
        }
    }
}

