/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.hotrestart;

import com.hazelcast.hotrestart.BackupTaskState;
import com.hazelcast.hotrestart.BackupTaskStatus;
import com.hazelcast.hotrestart.HotRestartService;

public class NoOpHotRestartService
implements HotRestartService {
    private static final BackupTaskStatus NO_TASK_BACKUP_STATUS = new BackupTaskStatus(BackupTaskState.NO_TASK, 0, 0);

    @Override
    public void backup() {
    }

    @Override
    public void backup(long backupSeq) {
    }

    @Override
    public BackupTaskStatus getBackupTaskStatus() {
        return NO_TASK_BACKUP_STATUS;
    }

    @Override
    public void interruptLocalBackupTask() {
    }

    @Override
    public void interruptBackupTask() {
    }

    @Override
    public boolean isHotBackupEnabled() {
        return false;
    }

    @Override
    public String getBackupDirectory() {
        return null;
    }
}

