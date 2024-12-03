/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.durableexecutor.impl.DurableExecutorContainer;
import com.hazelcast.durableexecutor.impl.operations.AbstractDurableExecutorOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;
import java.util.concurrent.Callable;

public class TaskBackupOperation
extends AbstractDurableExecutorOperation
implements BackupOperation {
    private int sequence;
    private Data callableData;

    public TaskBackupOperation() {
    }

    public TaskBackupOperation(String name, int sequence, Data callableData) {
        super(name);
        this.sequence = sequence;
        this.callableData = callableData;
    }

    @Override
    public void run() throws Exception {
        DurableExecutorContainer executorContainer = this.getExecutorContainer();
        Callable callable = (Callable)this.getNodeEngine().toObject(this.callableData);
        executorContainer.putBackup(this.sequence, callable);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.sequence);
        out.writeData(this.callableData);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.sequence = in.readInt();
        this.callableData = in.readData();
    }

    @Override
    public int getId() {
        return 7;
    }
}

