/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupContainerReader;
import java.io.File;

public interface BackupContainerReaderFactory {
    public BackupContainerReader createBackupContainerReader(File var1) throws BackupRestoreException;
}

