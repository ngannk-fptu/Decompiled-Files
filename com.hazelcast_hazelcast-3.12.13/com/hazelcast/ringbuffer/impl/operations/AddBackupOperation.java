/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.impl.operations.AbstractRingBufferOperation;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class AddBackupOperation
extends AbstractRingBufferOperation
implements BackupOperation {
    private long sequenceId;
    private Data item;

    public AddBackupOperation() {
    }

    public AddBackupOperation(String name, long sequenceId, Data item) {
        super(name);
        this.sequenceId = sequenceId;
        this.item = item;
    }

    @Override
    public void run() throws Exception {
        this.getRingBufferContainer().set(this.sequenceId, this.item);
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.sequenceId);
        out.writeData(this.item);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.sequenceId = in.readLong();
        this.item = in.readData();
    }
}

