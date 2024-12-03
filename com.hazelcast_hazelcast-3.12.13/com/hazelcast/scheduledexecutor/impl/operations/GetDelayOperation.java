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
import java.util.concurrent.TimeUnit;

public class GetDelayOperation
extends AbstractSchedulerOperation
implements ReadonlyOperation {
    private String taskName;
    private TimeUnit unit;
    private long response;

    public GetDelayOperation() {
    }

    public GetDelayOperation(ScheduledTaskHandler descriptor, TimeUnit unit) {
        super(descriptor.getSchedulerName());
        this.taskName = descriptor.getTaskName();
        this.unit = unit;
        this.setPartitionId(descriptor.getPartitionId());
    }

    @Override
    public void run() throws Exception {
        this.response = this.getContainer().getDelay(this.taskName, this.unit);
    }

    @Override
    public Long getResponse() {
        return this.response;
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.taskName);
        out.writeUTF(this.unit.name());
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.taskName = in.readUTF();
        this.unit = TimeUnit.valueOf(in.readUTF());
    }
}

