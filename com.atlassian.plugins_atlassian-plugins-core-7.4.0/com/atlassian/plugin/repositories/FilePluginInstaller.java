/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.CopyOnWriteMap
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.repositories;

import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.RevertablePluginInstaller;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.CopyOnWriteMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePluginInstaller
implements RevertablePluginInstaller {
    private static final Logger log = LoggerFactory.getLogger(FilePluginInstaller.class);
    public static final String ORIGINAL_PREFIX = ".original-";
    private final File directory;
    private final Map<String, BackupRepresentation> installedPlugins = CopyOnWriteMap.builder().stableViews().newHashMap();

    public FilePluginInstaller(File directory) {
        this.directory = (File)Preconditions.checkNotNull((Object)directory);
        Preconditions.checkState((boolean)directory.exists(), (String)"The plugin installation directory must exist, %s", (Object)directory.getAbsolutePath());
    }

    @Override
    public void installPlugin(String key, PluginArtifact pluginArtifact) {
        Preconditions.checkNotNull((Object)key, (Object)"The plugin key must be specified");
        Preconditions.checkNotNull((Object)pluginArtifact, (Object)"The plugin artifact must not be null");
        File newPluginFile = new File(this.directory, pluginArtifact.getName());
        try {
            this.backup(key, newPluginFile);
            if (newPluginFile.exists()) {
                newPluginFile.delete();
            }
        }
        catch (IOException e) {
            log.warn("Unable to backup old file", (Throwable)e);
        }
        try (FileOutputStream os = new FileOutputStream(newPluginFile);
             InputStream in = pluginArtifact.getInputStream();){
            IOUtils.copy((InputStream)in, (OutputStream)os);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not install plugin: " + pluginArtifact, e);
        }
    }

    @Override
    public void revertInstalledPlugin(String pluginKey) {
        BackupRepresentation backup = this.installedPlugins.get(pluginKey);
        if (backup != null) {
            File currentFile = new File(backup.getBackupFile().getParent(), backup.getCurrentPluginFilename());
            if (currentFile.exists()) {
                currentFile.delete();
            }
            if (backup.isUpgrade()) {
                try {
                    FileUtils.moveFile((File)backup.getBackupFile(), (File)new File(backup.getBackupFile().getParent(), backup.getOriginalPluginArtifactFilename()));
                }
                catch (IOException e) {
                    log.warn("Unable to restore old plugin for {}", (Object)pluginKey);
                }
            }
        }
    }

    @Override
    public void clearBackups() {
        File[] files = this.directory.listFiles(new BackupNameFilter());
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        this.installedPlugins.clear();
    }

    private void backup(String pluginKey, File currentPluginArtifact) throws IOException {
        BackupRepresentation orig = null;
        if (!this.installedPlugins.containsKey(pluginKey)) {
            orig = this.getBackupRepresentation(pluginKey, currentPluginArtifact);
        } else {
            BackupRepresentation oldBackupFile = this.installedPlugins.get(pluginKey);
            orig = new BackupRepresentation(oldBackupFile, currentPluginArtifact.getName());
            File previousPluginFile = new File(oldBackupFile.getBackupFile().getParent(), oldBackupFile.getCurrentPluginFilename());
            if (previousPluginFile.exists()) {
                previousPluginFile.delete();
            }
        }
        this.installedPlugins.put(pluginKey, orig);
    }

    private BackupRepresentation getBackupRepresentation(String pluginKey, File currentPluginArtifact) throws IOException {
        if (currentPluginArtifact.exists()) {
            File backupFile = new File(currentPluginArtifact.getParent(), ORIGINAL_PREFIX + currentPluginArtifact.getName());
            if (backupFile.exists()) {
                throw new IOException("Existing backup found for plugin " + pluginKey + ". Cannot install.");
            }
            FileUtils.copyFile((File)currentPluginArtifact, (File)backupFile);
            return new BackupRepresentation(backupFile, currentPluginArtifact.getName());
        }
        return new BackupRepresentation(currentPluginArtifact, currentPluginArtifact.getName());
    }

    private static class BackupRepresentation {
        private final File backupFile;
        private final String originalPluginArtifactFilename;
        private final String currentPluginFilename;
        private final boolean isUpgrade;

        public BackupRepresentation(File backupFile, String originalPluginArtifactFilename) {
            this.backupFile = (File)Preconditions.checkNotNull((Object)backupFile, (Object)"backupFile");
            this.originalPluginArtifactFilename = (String)Preconditions.checkNotNull((Object)originalPluginArtifactFilename, (Object)"originalPluginArtifactFilename");
            this.isUpgrade = !backupFile.getName().equals(originalPluginArtifactFilename);
            this.currentPluginFilename = originalPluginArtifactFilename;
        }

        public BackupRepresentation(BackupRepresentation oldBackup, String currentPluginFilename) {
            this.backupFile = ((BackupRepresentation)Preconditions.checkNotNull((Object)oldBackup, (Object)"oldBackup")).backupFile;
            this.originalPluginArtifactFilename = oldBackup.originalPluginArtifactFilename;
            this.isUpgrade = oldBackup.isUpgrade;
            this.currentPluginFilename = (String)Preconditions.checkNotNull((Object)currentPluginFilename, (Object)"currentPluginFilename");
        }

        public File getBackupFile() {
            return this.backupFile;
        }

        public String getOriginalPluginArtifactFilename() {
            return this.originalPluginArtifactFilename;
        }

        public String getCurrentPluginFilename() {
            return this.currentPluginFilename;
        }

        public boolean isUpgrade() {
            return this.isUpgrade;
        }
    }

    private static class BackupNameFilter
    implements FilenameFilter {
        private BackupNameFilter() {
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith(FilePluginInstaller.ORIGINAL_PREFIX);
        }
    }
}

