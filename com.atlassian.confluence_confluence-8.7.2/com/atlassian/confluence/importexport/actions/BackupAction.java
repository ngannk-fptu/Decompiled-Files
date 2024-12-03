/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreJobConverter;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.importexport.DefaultExportContext;
import com.atlassian.confluence.importexport.actions.AbstractBackupRestoreAction;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.init.AdminUiProperties;
import com.atlassian.core.util.FileUtils;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import java.io.File;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
@WebSudoRequired
@SystemAdminOnly
public class BackupAction
extends AbstractBackupRestoreAction {
    private static final Logger log = LoggerFactory.getLogger(BackupAction.class);
    private static final long serialVersionUID = 1L;
    private String tempPath;
    private String archivePath;
    private String downloadPath;
    private boolean archiveBackup;
    private boolean backupAttachments;
    private GateKeeper gateKeeper;
    private AdminUiProperties adminUiProperties;
    private transient DarkFeatureManager salDarkFeatureManager;
    private BackupRestoreJobConverter backupRestoreJobConverter;

    @Override
    public String doDefault() throws Exception {
        this.backupAttachments = true;
        return super.doDefault();
    }

    public String getDownloadPath() {
        return this.downloadPath;
    }

    public String getArchivePath() {
        return this.archivePath;
    }

    public String getTempPath() {
        return this.tempPath;
    }

    public String execute() {
        try {
            this.tempPath = this.runLegacyVersion();
            if (this.isDownloadEnabled()) {
                this.downloadPath = this.prepareDownloadPath(this.tempPath);
                Predicate<User> permissionPredicate = u -> this.permissionManager.hasPermission((User)u, Permission.EXPORT, PermissionManager.TARGET_SYSTEM);
                this.gateKeeper.addKey(this.downloadPath, this.getAuthenticatedUser(), permissionPredicate);
            }
            if (this.archiveBackup) {
                int lastSeparator = this.tempPath.lastIndexOf(File.separator);
                String exportFilename = this.tempPath.substring(lastSeparator + 1);
                String backupDir = this.settingsManager.getGlobalSettings().getBackupPath();
                this.archivePath = backupDir + File.separator + exportFilename;
                FileUtils.copyFile((File)new File(this.tempPath), (File)new File(this.archivePath), (boolean)false);
            }
            return "success";
        }
        catch (Exception e) {
            log.error("Error backing up the site: " + e, (Throwable)e);
            this.addActionError(this.getText("backup.site.failed"));
            return "error";
        }
    }

    private String runLegacyVersion() throws BackupRestoreException {
        DefaultExportContext exportContext = DefaultExportContext.getXmlBackupInstance();
        exportContext.setExportAttachments(this.isBackupAttachments());
        try {
            String filePath = this.getImportExportManager().exportAs(exportContext, new ProgressMeter());
            return filePath;
        }
        catch (Exception e) {
            throw new BackupRestoreException(e);
        }
    }

    private String prepareDownloadPath(String path) {
        String exportDir = this.getBootstrapManager().getFilePathProperty("struts.multipart.saveDir");
        int exportDirIndex = path.indexOf(exportDir);
        if (exportDirIndex != -1) {
            path = path.substring(exportDirIndex + exportDir.length() + 1);
        }
        return this.getBootstrapStatusProvider().getWebAppContextPath() + "/download/export/" + StringUtils.defaultString((String)path).replaceAll("\\\\", "/");
    }

    public void setArchiveBackup(boolean archiveBackup) {
        this.archiveBackup = archiveBackup;
    }

    public boolean isBackupAttachments() {
        return this.backupAttachments;
    }

    public void setBackupAttachments(boolean backupAttachments) {
        this.backupAttachments = backupAttachments;
    }

    public boolean isDownloadEnabled() {
        return this.adminUiProperties.isAllowed("admin.ui.allow.manual.backup.download");
    }

    public void setGateKeeper(GateKeeper gateKeeper) {
        this.gateKeeper = gateKeeper;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public void setAdminUiProperties(AdminUiProperties adminUiProperties) {
        this.adminUiProperties = adminUiProperties;
    }

    public void setBackupRestoreJobConverter(BackupRestoreJobConverter backupRestoreJobConverter) {
        this.backupRestoreJobConverter = backupRestoreJobConverter;
    }
}

