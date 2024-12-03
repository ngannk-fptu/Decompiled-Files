/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 */
package com.atlassian.confluence.impl.backuprestore.backup.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.models.AttachmentInfo;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProvider;
import com.atlassian.plugin.ModuleDescriptor;
import java.io.File;
import java.util.Collection;
import java.util.List;

public interface BackupContainerWriter
extends AutoCloseable {
    public void writeObjects(Collection<EntityObjectReadyForExport> var1) throws BackupRestoreException;

    public void addAttachments(Collection<AttachmentInfo> var1);

    public void addDescriptionProperty(String var1, String var2) throws BackupRestoreException;

    public void addPluginModuleData(List<? extends ModuleDescriptor<BackupRestoreProvider>> var1) throws BackupRestoreException;

    @Override
    public void close() throws BackupRestoreException;

    public File getOutputFile();
}

