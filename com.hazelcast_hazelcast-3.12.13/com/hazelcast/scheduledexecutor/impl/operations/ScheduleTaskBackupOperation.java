/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.impl.TaskDefinition;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractSchedulerOperation;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class ScheduleTaskBackupOperation
extends AbstractSchedulerOperation
implements BackupOperation {
    private TaskDefinition definition;

    public ScheduleTaskBackupOperation() {
    }

    public ScheduleTaskBackupOperation(String schedulerName, TaskDefinition definition) {
        super(schedulerName);
        this.definition = definition;
    }

    @Override
    public void run() throws Exception {
        this.getContainer().enqueueSuspended(this.definition);
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.definition);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.definition = (TaskDefinition)in.readObject();
    }
}

