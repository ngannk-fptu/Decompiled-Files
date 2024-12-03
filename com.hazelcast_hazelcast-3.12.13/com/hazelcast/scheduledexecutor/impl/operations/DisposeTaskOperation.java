/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractBackupAwareSchedulerOperation;
import com.hazelcast.scheduledexecutor.impl.operations.DisposeBackupTaskOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class DisposeTaskOperation
extends AbstractBackupAwareSchedulerOperation
implements MutatingOperation {
    private String taskName;

    public DisposeTaskOperation() {
    }

    public DisposeTaskOperation(ScheduledTaskHandler descriptor) {
        super(descriptor.getSchedulerName());
        this.taskName = descriptor.getTaskName();
        this.setPartitionId(descriptor.getPartitionId());
    }

    @Override
    public void run() throws Exception {
        this.getContainer().dispose(this.taskName);
    }

    @Override
    public Operation getBackupOperation() {
        return new DisposeBackupTaskOperation(this.getSchedulerName(), this.taskName);
    }

    @Override
    public int getId() {
        return 20;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.taskName);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.taskName = in.readUTF();
    }
}

