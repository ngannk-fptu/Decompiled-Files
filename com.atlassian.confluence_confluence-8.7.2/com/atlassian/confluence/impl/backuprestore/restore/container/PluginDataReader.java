/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.PluginExportCompatibility
 *  com.atlassian.confluence.upgrade.VersionNumberComparator
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.container.PluginDataWriter;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupProperties;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProvider;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProviderManager;
import com.atlassian.confluence.plugin.descriptor.backup.BackupRestoreProviderModuleDescriptor;
import com.atlassian.confluence.upgrade.PluginExportCompatibility;
import com.atlassian.confluence.upgrade.VersionNumberComparator;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginDataReader {
    private static final Logger log = LoggerFactory.getLogger(PluginDataReader.class);
    private final ZipFile zipFile;
    private final BackupRestoreProviderManager backupRestoreProviderManager;
    private final PluginAccessor pluginAccessor;

    public PluginDataReader(ZipFile zipFile, BackupRestoreProviderManager backupRestoreProviderManager, PluginAccessor pluginAccessor) {
        this.backupRestoreProviderManager = backupRestoreProviderManager;
        this.zipFile = zipFile;
        this.pluginAccessor = pluginAccessor;
    }

    public void readPluginData(BackupProperties backupProperties) throws BackupRestoreException {
        if (this.skipPluginData(backupProperties)) {
            return;
        }
        for (BackupRestoreProviderModuleDescriptor moduleDescriptor : this.backupRestoreProviderManager.getModuleDescriptors()) {
            try {
                ZipEntry zipEntry = this.zipFile.getEntry(PluginDataWriter.getPathInZip((ModuleDescriptor<BackupRestoreProvider>)moduleDescriptor));
                if (zipEntry == null) {
                    log.warn("No plugin data found for module descriptor: {}", (Object)moduleDescriptor.getCompleteKey());
                    return;
                }
                InputStream is = this.zipFile.getInputStream(zipEntry);
                try {
                    moduleDescriptor.getModule().restore(is);
                }
                finally {
                    if (is == null) continue;
                    is.close();
                }
            }
            catch (IOException ex) {
                throw new BackupRestoreException(String.format("IOException while importing plugin data for : %s", moduleDescriptor.getCompleteKey()), ex);
            }
            catch (ImportExportException ex) {
                throw new BackupRestoreException(String.format("ImportExportException while importing plugin data for : %s", moduleDescriptor.getCompleteKey()), ex);
            }
        }
    }

    private boolean skipPluginData(BackupProperties backupProperties) {
        Map<String, PluginExportCompatibility> compatibilityMap = ExportDescriptor.getPluginExportCompatibility(backupProperties.getProperties());
        if (compatibilityMap == null || compatibilityMap.isEmpty()) {
            return false;
        }
        boolean allowPluginDataImport = true;
        VersionNumberComparator comparator = new VersionNumberComparator();
        for (Map.Entry<String, PluginExportCompatibility> compatibility : compatibilityMap.entrySet()) {
            Plugin plugin = this.pluginAccessor.getPlugin(compatibility.getKey());
            if (plugin == null || plugin.getPluginInformation() == null) {
                log.info("Couldn't check ActiveObjects data is compatible with {} because the plugin isn't installed", (Object)compatibility.getKey());
                continue;
            }
            String version = plugin.getPluginInformation().getVersion();
            if (version == null) {
                log.info("Couldn't check ActiveObjects data is compatible with {} because the version number is unavailable", (Object)compatibility.getKey());
                continue;
            }
            String earliestVersion = compatibility.getValue().getEarliestVersion();
            String createdByVersion = compatibility.getValue().getCurrentVersion();
            boolean allow = comparator.compare(earliestVersion, version) <= 0;
            if (allow) continue;
            log.info("Plugin data import will be skipped because the plugin {} version {} is required, and you are using {}. The backup was created with version {}.", new Object[]{compatibility.getKey(), earliestVersion, version, createdByVersion});
            allowPluginDataImport = false;
        }
        return !allowPluginDataImport;
    }
}

