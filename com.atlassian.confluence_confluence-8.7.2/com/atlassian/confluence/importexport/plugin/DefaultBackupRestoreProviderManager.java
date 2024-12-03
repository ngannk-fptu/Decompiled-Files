/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.importexport.plugin;

import com.atlassian.confluence.importexport.plugin.BackupRestoreProvider;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProviderManager;
import com.atlassian.confluence.plugin.descriptor.backup.BackupRestoreProviderModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DefaultBackupRestoreProviderManager
implements BackupRestoreProviderManager {
    private static final String PLUGIN_DATA_EXPORT_DIR = "plugin-data";
    private static final String DATA_FILE_EXT = ".pdata";
    private final PluginAccessor pluginAccessor;

    public DefaultBackupRestoreProviderManager(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public List<BackupRestoreProviderModuleDescriptor> getModuleDescriptors() {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(BackupRestoreProviderModuleDescriptor.class);
    }

    @Override
    public File getModuleBackupFile(File exportBaseDir, ModuleDescriptor<BackupRestoreProvider> moduleDescriptor) throws IOException {
        File pluginExportDir = this.getPluginExportDir(exportBaseDir, moduleDescriptor);
        return new File(pluginExportDir + File.separator + moduleDescriptor.getKey() + DATA_FILE_EXT);
    }

    @Override
    public File createModuleBackupFile(File exportBaseDir, ModuleDescriptor<BackupRestoreProvider> moduleDescriptor) throws IOException {
        File pluginExportDir = this.getPluginExportDir(exportBaseDir, moduleDescriptor);
        if (!pluginExportDir.exists()) {
            pluginExportDir.mkdirs();
        }
        File moduleExportFile = this.getModuleBackupFile(exportBaseDir, moduleDescriptor);
        moduleExportFile.createNewFile();
        return moduleExportFile;
    }

    private File getPluginExportDir(File exportBaseDir, ModuleDescriptor<BackupRestoreProvider> moduleDescriptor) {
        return new File(exportBaseDir + File.separator + PLUGIN_DATA_EXPORT_DIR + File.separator + moduleDescriptor.getPluginKey());
    }
}

