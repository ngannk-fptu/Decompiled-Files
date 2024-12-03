/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 */
package com.atlassian.confluence.importexport.plugin;

import com.atlassian.confluence.importexport.plugin.BackupRestoreProvider;
import com.atlassian.confluence.plugin.descriptor.backup.BackupRestoreProviderModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface BackupRestoreProviderManager {
    public List<BackupRestoreProviderModuleDescriptor> getModuleDescriptors();

    public File getModuleBackupFile(File var1, ModuleDescriptor<BackupRestoreProvider> var2) throws IOException;

    public File createModuleBackupFile(File var1, ModuleDescriptor<BackupRestoreProvider> var2) throws IOException;
}

