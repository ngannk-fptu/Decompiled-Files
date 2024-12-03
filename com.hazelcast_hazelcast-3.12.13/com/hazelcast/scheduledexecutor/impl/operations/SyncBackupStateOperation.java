/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.scheduledexecutor.impl.ScheduledTaskResult;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskStatisticsImpl;
import com.hazelcast.scheduledexecutor.impl.operations.SyncStateOperation;
import java.util.Map;

public class SyncBackupStateOperation
extends SyncStateOperation {
    public SyncBackupStateOperation() {
    }

    public SyncBackupStateOperation(String schedulerName, String taskName, Map state, ScheduledTaskStatisticsImpl stats, ScheduledTaskResult result) {
        super(schedulerName, taskName, state, stats, result);
    }

    @Override
    public void run() throws Exception {
        this.getContainer().syncState(this.taskName, this.state, this.stats, this.result);
    }

    @Override
    public int getId() {
        return 18;
    }
}

