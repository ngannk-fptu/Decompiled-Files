/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractBackupAwareSchedulerOperation;
import com.hazelcast.scheduledexecutor.impl.operations.CancelTaskBackupOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class CancelTaskOperation
extends AbstractBackupAwareSchedulerOperation
implements MutatingOperation {
    private String taskName;
    private boolean mayInterruptIfRunning;
    private boolean response;

    public CancelTaskOperation() {
    }

    public CancelTaskOperation(ScheduledTaskHandler descriptor, boolean mayInterruptIfRunning) {
        super(descriptor.getSchedulerName());
        this.taskName = descriptor.getTaskName();
        this.mayInterruptIfRunning = mayInterruptIfRunning;
        this.setPartitionId(descriptor.getPartitionId());
    }

    @Override
    public void run() throws Exception {
        this.response = this.getContainer().cancel(this.taskName);
    }

    @Override
    public Boolean getResponse() {
        return this.response;
    }

    @Override
    public int getId() {
        return 8;
    }

    @Override
    public Operation getBackupOperation() {
        return new CancelTaskBackupOperation(this.schedulerName, this.taskName);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.taskName);
        out.writeBoolean(this.mayInterruptIfRunning);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.taskName = in.readUTF();
        this.mayInterruptIfRunning = in.readBoolean();
    }
}

