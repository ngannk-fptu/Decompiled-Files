/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskResult;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskStatisticsImpl;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractBackupAwareSchedulerOperation;
import com.hazelcast.scheduledexecutor.impl.operations.SyncBackupStateOperation;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.Map;

public class SyncStateOperation
extends AbstractBackupAwareSchedulerOperation {
    protected String taskName;
    protected Map<Object, Object> state;
    protected ScheduledTaskStatisticsImpl stats;
    protected ScheduledTaskResult result;
    private boolean shouldRun;

    public SyncStateOperation() {
    }

    public SyncStateOperation(String schedulerName, String taskName, Map state, ScheduledTaskStatisticsImpl stats, ScheduledTaskResult result) {
        super(schedulerName);
        this.taskName = taskName;
        this.state = state;
        this.stats = stats;
        this.result = result;
    }

    @Override
    public void run() throws Exception {
        int partitionId = this.getPartitionId();
        boolean bl = this.shouldRun = partitionId == -1;
        if (partitionId >= 0) {
            Address partitionOwner = this.getNodeEngine().getPartitionService().getPartitionOwner(partitionId);
            boolean bl2 = this.shouldRun = this.shouldRun || this.getCallerAddress().equals(partitionOwner);
        }
        if (this.shouldRun) {
            this.getContainer().syncState(this.taskName, this.state, this.stats, this.result);
        }
    }

    @Override
    public boolean shouldBackup() {
        return super.shouldBackup() && this.shouldRun;
    }

    @Override
    public Operation getBackupOperation() {
        return new SyncBackupStateOperation(this.schedulerName, this.taskName, this.state, this.stats, this.result);
    }

    @Override
    public int getId() {
        return 17;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.taskName);
        SerializationUtil.writeMap(this.state, out);
        out.writeObject(this.stats);
        out.writeObject(this.result);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.taskName = in.readUTF();
        this.state = SerializationUtil.readMap(in);
        this.stats = (ScheduledTaskStatisticsImpl)in.readObject();
        this.result = (ScheduledTaskResult)in.readObject();
    }
}

