/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.xmlimport.BackupImporter;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.user.User;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class AbstractBackupRestoreAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(AbstractBackupRestoreAction.class);
    private ImportExportManager importExportManager;
    private BuildInformation buildInformation = BuildInformation.INSTANCE;
    private ApplicationProperties applicationProperties;
    private boolean synchronous = false;
    protected FilesystemPath confluenceHome;
    protected ZduManager zduManager;

    public boolean isSynchronous() {
        return this.synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public void setImportExportManager(ImportExportManager importExportManager) {
        this.importExportManager = importExportManager;
    }

    public void setConfluenceHome(FilesystemPath confluenceHome) {
        this.confluenceHome = confluenceHome;
    }

    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public String getSystemFileSeparator() {
        return File.separator;
    }

    public List<String> getFilenames() {
        return this.getFilesInRestoreFolder().stream().map(file -> {
            String parent = file.getParentFile().getName();
            if (parent.toLowerCase(Locale.ROOT).equals("restore")) {
                return file.getName();
            }
            return Path.of(parent, file.getName()).toString();
        }).collect(Collectors.toList());
    }

    public List<File> getFilesInRestoreFolder() {
        ArrayList<File> files = new ArrayList<File>();
        LinkedList<String> restoreDirs = new LinkedList<String>();
        restoreDirs.add(Path.of("restore", "space").toString());
        restoreDirs.add(Path.of("restore", "site").toString());
        restoreDirs.add("restore");
        for (String restoreDir : restoreDirs) {
            File restoreDirectory = new File(this.getConfluenceHome(), restoreDir);
            if (restoreDirectory.isDirectory()) {
                files.addAll(Arrays.asList(restoreDirectory.listFiles(File::isFile)));
                continue;
            }
            if (restoreDirectory.mkdirs()) continue;
            log.error("Error trying to create restore directory [" + restoreDirectory + "] in confluence home.");
        }
        if (!files.isEmpty()) {
            Collections.sort(files, (o1, o2) -> {
                File file1 = o1;
                File file2 = o2;
                return Long.compare(file1.lastModified(), file2.lastModified());
            });
        }
        Collections.reverse(files);
        return files;
    }

    @Nonnull
    protected File getConfluenceHome() {
        if (this.confluenceHome != null) {
            return this.confluenceHome.asJavaFile();
        }
        return new File(this.applicationProperties.getHomeDirectory().getPath());
    }

    public boolean isZduEnabled() {
        return this.zduManager.getUpgradeStatus().getState() == ZduStatus.State.ENABLED;
    }

    public void setZduManager(ZduManager zduManager) {
        this.zduManager = zduManager;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    protected ImportExportManager getImportExportManager() {
        return this.importExportManager;
    }

    public BuildInformation getBuildInformation() {
        return this.buildInformation;
    }

    public String getMinimumFullImportVersion() {
        return BackupImporter.MINIMUM_FULL_IMPORT_BUILD_NUMBER.getVersion();
    }

    public String getFullExportBackwardsCompatibility() {
        return BackupImporter.FULL_EXPORT_BACKWARDS_COMPATIBILITY.getVersion();
    }

    public String getMinimumSpaceImportVersion() {
        return BackupImporter.MINIMUM_SPACE_IMPORT_BUILD_NUMBER.getVersion();
    }
}

