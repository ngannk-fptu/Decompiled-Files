/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.durableexecutor.impl.DurableExecutorContainer;
import com.hazelcast.durableexecutor.impl.operations.AbstractDurableExecutorOperation;
import com.hazelcast.durableexecutor.impl.operations.TaskBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.concurrent.Callable;

public class TaskOperation
extends AbstractDurableExecutorOperation
implements BackupAwareOperation,
MutatingOperation {
    private Data callableData;
    private transient int sequence;
    private transient Callable callable;

    public TaskOperation() {
    }

    public TaskOperation(String name, Data callableData) {
        super(name);
        this.callableData = callableData;
    }

    @Override
    public void run() throws Exception {
        this.callable = (Callable)this.getNodeEngine().toObject(this.callableData);
        DurableExecutorContainer executorContainer = this.getExecutorContainer();
        this.sequence = executorContainer.execute(this.callable);
    }

    @Override
    public Object getResponse() {
        return this.sequence;
    }

    @Override
    public Operation getBackupOperation() {
        return new TaskBackupOperation(this.name, this.sequence, this.callableData);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.callableData);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.callableData = in.readData();
    }

    @Override
    public int getId() {
        return 8;
    }
}

