/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.container.ArchiveWriter;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProvider;
import com.atlassian.plugin.ModuleDescriptor;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginDataWriter {
    private static final Logger log = LoggerFactory.getLogger(PluginDataWriter.class);
    public static final String PLUGIN_DATA_EXPORT_DIR = "plugin-data";
    public static final String DATA_FILE_EXT = ".pdata";
    private final ArchiveWriter archiveWriter;

    public PluginDataWriter(ArchiveWriter archiveWriter) {
        this.archiveWriter = archiveWriter;
    }

    public void writePluginData(List<? extends ModuleDescriptor<BackupRestoreProvider>> moduleDescriptors) throws BackupRestoreException {
        for (ModuleDescriptor<BackupRestoreProvider> moduleDescriptor : moduleDescriptors) {
            try (PipedOutputStream outputStream = new PipedOutputStream();){
                PipedInputStream inputStream = new PipedInputStream(outputStream);
                this.archiveWriter.compressFromStream(inputStream, PluginDataWriter.getPathInZip(moduleDescriptor));
                ((BackupRestoreProvider)moduleDescriptor.getModule()).backup(outputStream);
            }
            catch (ImportExportException | IOException e) {
                throw new BackupRestoreException(e);
            }
        }
    }

    public static String getPathInZip(ModuleDescriptor<BackupRestoreProvider> moduleDescriptor) {
        String pathInZip = "plugin-data/" + moduleDescriptor.getPluginKey() + "/" + moduleDescriptor.getKey() + DATA_FILE_EXT;
        log.debug("Path in zip for plugin data: {}", (Object)pathInZip);
        return pathInZip;
    }
}

