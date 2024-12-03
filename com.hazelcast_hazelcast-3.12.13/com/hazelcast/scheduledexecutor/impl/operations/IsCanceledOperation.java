/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractSchedulerOperation;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class IsCanceledOperation
extends AbstractSchedulerOperation
implements ReadonlyOperation {
    private String taskName;
    private boolean response;

    public IsCanceledOperation() {
    }

    public IsCanceledOperation(ScheduledTaskHandler descriptor) {
        super(descriptor.getSchedulerName());
        this.taskName = descriptor.getTaskName();
        this.setPartitionId(descriptor.getPartitionId());
    }

    @Override
    public void run() throws Exception {
        this.response = this.getContainer().isCancelled(this.taskName);
    }

    @Override
    public Boolean getResponse() {
        return this.response;
    }

    @Override
    public int getId() {
        return 14;
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

