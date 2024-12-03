/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.confluence.impl.backuprestore.restore.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupContainerReader;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupContainerReaderFactory;
import com.atlassian.confluence.impl.backuprestore.restore.container.PluginDataReader;
import com.atlassian.confluence.impl.backuprestore.restore.container.XMLBackupContainerReader;
import com.atlassian.confluence.impl.backuprestore.restore.preprocessing.ImportedObjectV1ToV2Converter;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProviderManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.LazyReference;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

public class BackupContainerReaderFactoryImpl
implements BackupContainerReaderFactory {
    private final LazyReference<ImportedObjectV1ToV2Converter> importedObjectV1ToV2ConverterLazyReference = new LazyComponentReference("importedObjectV1ToV2Converter");
    private final LazyReference<BackupRestoreProviderManager> backupRestoreProviderManagerLazyReferencee = new LazyComponentReference("backupRestoreProviderManager");
    private final LazyReference<PluginAccessor> pluginAccessorLazyReference = new LazyComponentReference("pluginAccessor");

    @Override
    public BackupContainerReader createBackupContainerReader(File file) throws BackupRestoreException {
        try {
            if (!file.exists()) {
                throw new BackupRestoreException("Backup file not found: " + file.getCanonicalPath());
            }
            ZipFile zipFile = new ZipFile(file);
            PluginDataReader pluginDataReader = new PluginDataReader(zipFile, (BackupRestoreProviderManager)this.backupRestoreProviderManagerLazyReferencee.get(), (PluginAccessor)this.pluginAccessorLazyReference.get());
            return new XMLBackupContainerReader(zipFile, (ImportedObjectV1ToV2Converter)this.importedObjectV1ToV2ConverterLazyReference.get(), pluginDataReader);
        }
        catch (IOException e) {
            throw new BackupRestoreException(e);
        }
    }
}

