/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.durableexecutor.impl.operations.AbstractDurableExecutorOperation;
import com.hazelcast.durableexecutor.impl.operations.DurableExecutorWaitNotifyKey;
import com.hazelcast.durableexecutor.impl.operations.PutResultBackupOperation;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public class PutResultOperation
extends AbstractDurableExecutorOperation
implements Notifier,
BackupAwareOperation {
    private int sequence;
    private Object result;

    public PutResultOperation() {
    }

    public PutResultOperation(String name, int sequence, Object result) {
        super(name);
        this.sequence = sequence;
        this.result = result;
    }

    @Override
    public void run() throws Exception {
        this.getExecutorContainer().putResult(this.sequence, this.result);
    }

    @Override
    public boolean shouldNotify() {
        return true;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        long uniqueId = Bits.combineToLong(this.getPartitionId(), this.sequence);
        return new DurableExecutorWaitNotifyKey(this.name, uniqueId);
    }

    @Override
    public Operation getBackupOperation() {
        return new PutResultBackupOperation(this.name, this.sequence, this.result);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.sequence);
        out.writeObject(this.result);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.sequence = in.readInt();
        this.result = in.readObject();
    }

    @Override
    public int getId() {
        return 2;
    }
}

