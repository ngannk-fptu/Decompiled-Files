/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.hotrestart;

import com.hazelcast.hotrestart.BackupTaskStatus;

public interface HotRestartService {
    public static final String BACKUP_DIR_PREFIX = "backup-";

    public void backup();

    public void backup(long var1);

    public BackupTaskStatus getBackupTaskStatus();

    public void interruptLocalBackupTask();

    public void interruptBackupTask();

    public boolean isHotBackupEnabled();

    public String getBackupDirectory();
}

