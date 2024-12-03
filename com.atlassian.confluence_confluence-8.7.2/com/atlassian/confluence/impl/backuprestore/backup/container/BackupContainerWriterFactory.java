/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.container.BackupContainerWriter;
import java.io.IOException;

public interface BackupContainerWriterFactory {
    public BackupContainerWriter createBackupContainerWriter(String var1) throws BackupRestoreException, IOException;
}

