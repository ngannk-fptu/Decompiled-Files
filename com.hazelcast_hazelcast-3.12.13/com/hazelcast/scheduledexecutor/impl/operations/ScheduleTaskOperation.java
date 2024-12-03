/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.impl.TaskDefinition;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractBackupAwareSchedulerOperation;
import com.hazelcast.scheduledexecutor.impl.operations.ScheduleTaskBackupOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class ScheduleTaskOperation
extends AbstractBackupAwareSchedulerOperation
implements MutatingOperation {
    private Object definition;

    public ScheduleTaskOperation() {
    }

    public ScheduleTaskOperation(String schedulerName, TaskDefinition definition) {
        super(schedulerName);
        this.definition = definition;
    }

    @Override
    public void run() throws Exception {
        this.getContainer().schedule((TaskDefinition)this.definition);
    }

    @Override
    public int getId() {
        return 6;
    }

    @Override
    public Operation getBackupOperation() {
        return new ScheduleTaskBackupOperation(this.schedulerName, (TaskDefinition)this.definition);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.definition);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.definition = in.readObject();
    }
}

