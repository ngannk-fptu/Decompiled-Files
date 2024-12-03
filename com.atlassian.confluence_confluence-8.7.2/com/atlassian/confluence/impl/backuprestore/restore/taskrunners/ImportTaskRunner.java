/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.taskrunners;

import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupProperties;

public interface ImportTaskRunner
extends AutoCloseable {
    public void runPreImportTasks() throws BackupRestoreException;

    public void runPostImportTasks(BackupRestoreJob var1, BackupRestoreSettings var2, BackupProperties var3) throws BackupRestoreException;

    @Override
    public void close();
}

