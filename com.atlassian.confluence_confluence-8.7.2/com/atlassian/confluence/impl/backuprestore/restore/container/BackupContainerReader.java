/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreConsumer;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupProperties;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.Properties;

public interface BackupContainerReader
extends AutoCloseable {
    public BackupProperties getBackupProperties() throws BackupRestoreException;

    @Deprecated
    public Properties getLegacyBackupProperties() throws BackupRestoreException;

    public void readObjects(BackupRestoreConsumer<ImportedObjectV2> var1) throws BackupRestoreException;

    public void readPluginModuleData() throws BackupRestoreException;

    @Override
    public void close() throws BackupRestoreException;
}

