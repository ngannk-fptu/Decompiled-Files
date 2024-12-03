/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.scheduledexecutor.impl.operations.AbstractSchedulerOperation;
import com.hazelcast.spi.BackupAwareOperation;

public abstract class AbstractBackupAwareSchedulerOperation
extends AbstractSchedulerOperation
implements BackupAwareOperation {
    AbstractBackupAwareSchedulerOperation() {
    }

    AbstractBackupAwareSchedulerOperation(String schedulerName) {
        super(schedulerName);
    }

    @Override
    public boolean shouldBackup() {
        boolean isMemberOperation = this.getPartitionId() == -1;
        return !isMemberOperation;
    }

    @Override
    public int getSyncBackupCount() {
        return this.getContainer().getDurability();
    }

    @Override
    public int getAsyncBackupCount() {
        return 0;
    }
}

