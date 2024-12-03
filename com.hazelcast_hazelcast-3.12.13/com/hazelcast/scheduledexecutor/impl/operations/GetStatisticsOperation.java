/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.ScheduledTaskStatistics;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractSchedulerOperation;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class GetStatisticsOperation
extends AbstractSchedulerOperation
implements ReadonlyOperation {
    private String taskName;
    private ScheduledTaskStatistics response;

    public GetStatisticsOperation() {
    }

    public GetStatisticsOperation(ScheduledTaskHandler handler) {
        super(handler.getSchedulerName());
        this.taskName = handler.getTaskName();
        this.setPartitionId(handler.getPartitionId());
    }

    @Override
    public void run() throws Exception {
        this.response = this.getContainer().getStatistics(this.taskName);
    }

    @Override
    public ScheduledTaskStatistics getResponse() {
        return this.response;
    }

    @Override
    public int getId() {
        return 15;
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

