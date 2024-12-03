/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.core.util.PairType
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.event.Event
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.admin.ConfigurationEvent;
import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.init.AdminUiProperties;
import com.atlassian.core.util.PairType;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.event.Event;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@AdminOnly
public class DailyBackupAdminAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(DailyBackupAdminAction.class);
    private ScheduledJobManager scheduledJobManager;
    private String backupPath;
    private boolean backupAttachments;
    private String backupOption;
    private String dailyBackupFilePrefix;
    private String dailyBackupDateFormatPattern;
    private boolean editMode = true;
    private AdminUiProperties adminUiProperties;
    private FilesystemPath confluenceHome;

    @Override
    public void validate() {
        if (StringUtils.isNotEmpty((CharSequence)this.getBackupOption()) && "custom".equals(this.getBackupOption())) {
            if (!StringUtils.isNotEmpty((CharSequence)this.getBackupPath())) {
                this.addActionError("Backup path is a required field.");
            } else {
                File backupPathFile = new File(this.getBackupPath());
                if (!backupPathFile.exists()) {
                    this.addFieldError("backupPath", "Backup path specified does not exist.");
                } else if (!backupPathFile.isDirectory()) {
                    this.addActionError("The backup path cannot be file.");
                } else if (!backupPathFile.canWrite()) {
                    this.addActionError("Confluence does not have permissions to write to the backup directory specified.");
                }
            }
        }
        if (!StringUtils.isNotEmpty((CharSequence)this.getDailyBackupFilePrefix())) {
            this.addActionError("You must specify a value for the daily backup file prefix.");
        }
        try {
            new SimpleDateFormat(this.getDailyBackupDateFormatPattern());
        }
        catch (IllegalArgumentException e) {
            this.addActionError("Daily backup date pattern invalid.");
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() throws Exception {
        this.editMode = false;
        return this.doDefault();
    }

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doDefault() throws Exception {
        this.backupPath = this.settingsManager.getGlobalSettings().getBackupPath();
        boolean doBackups = this.settingsManager.getGlobalSettings().isBackupDaily();
        this.backupAttachments = this.settingsManager.getGlobalSettings().isBackupAttachmentsDaily();
        this.dailyBackupFilePrefix = this.settingsManager.getGlobalSettings().getDailyBackupFilePrefix();
        this.dailyBackupDateFormatPattern = this.settingsManager.getGlobalSettings().getDailyBackupDateFormatPattern();
        this.backupOption = !StringUtils.isNotEmpty((CharSequence)this.backupPath) || !doBackups ? "disable" : (this.getDefaultBackupPath().equals(this.backupPath) ? "default" : "custom");
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public String execute() throws Exception {
        this.eventManager.publishEvent((Event)new ConfigurationEvent(this));
        return "success";
    }

    public boolean isBackupEnabled() {
        return ScheduleUtil.isBackupEnabled(this.scheduledJobManager, this.settingsManager);
    }

    public String getDefaultBackupPath() {
        if (this.confluenceHome != null) {
            return this.confluenceHome.path(new String[]{"backups"}).asJavaFile().getAbsolutePath();
        }
        return this.getBootstrapManager().getConfluenceHome() + System.getProperty("file.separator") + "backups";
    }

    public String doEdit() throws ConfigurationException {
        Settings originalSettings = this.settingsManager.getGlobalSettings();
        Settings globalSettings = new Settings(originalSettings);
        if ("disable".equals(this.backupOption)) {
            globalSettings.setBackupDaily(false);
        } else {
            globalSettings.setBackupDaily(true);
            if ("default".equals(this.backupOption)) {
                globalSettings.setBackupPath(this.getDefaultBackupPath());
            } else if (this.isCustomLocationAllowed()) {
                globalSettings.setBackupPath(this.getBackupPath());
            } else if (!globalSettings.getBackupPath().equals(this.getBackupPath())) {
                log.warn("Attempt to write custom daily backup location when daily backup location modifications are disallowed.");
            }
        }
        globalSettings.setBackupAttachmentsDaily(this.isBackupAttachments());
        globalSettings.setDailyBackupFilePrefix(this.getDailyBackupFilePrefix());
        globalSettings.setDailyBackupDateFormatPattern(this.getDailyBackupDateFormatPattern());
        this.settingsManager.updateGlobalSettings(globalSettings);
        this.eventManager.publishEvent((Event)new GlobalSettingsChangedEvent(this, originalSettings, globalSettings, globalSettings.getBaseUrl(), globalSettings.getBaseUrl()));
        return "success";
    }

    public boolean isCustomLocationAllowed() {
        return this.adminUiProperties.isAllowed("admin.ui.allow.daily.backup.custom.location");
    }

    public String getExampleOfDatePattern() {
        return new SimpleDateFormat(this.getDailyBackupDateFormatPattern()).format(new Date());
    }

    public String getDailyBackupFilePrefixWithDatePattern() {
        return this.getDailyBackupFilePrefix() + this.getExampleOfDatePattern();
    }

    public String getBackupPath() {
        return this.backupPath;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }

    public boolean isBackupAttachments() {
        return this.backupAttachments;
    }

    public void setBackupAttachments(boolean backupAttachments) {
        this.backupAttachments = backupAttachments;
    }

    public String getBackupOption() {
        return this.backupOption;
    }

    public void setBackupOption(String backupOption) {
        this.backupOption = backupOption;
    }

    public List<PairType> getBackupOptions() {
        ArrayList<PairType> result = new ArrayList<PairType>();
        result.add(new PairType((Serializable)((Object)this.getText("daily.backup.default")), (Serializable)((Object)"default")));
        result.add(new PairType((Serializable)((Object)this.getText("daily.backup.custom")), (Serializable)((Object)"custom")));
        return result;
    }

    public String getDailyBackupFilePrefix() {
        return this.dailyBackupFilePrefix;
    }

    public void setDailyBackupFilePrefix(String dailyBackupFilePrefix) {
        this.dailyBackupFilePrefix = dailyBackupFilePrefix;
    }

    public String getDailyBackupDateFormatPattern() {
        return this.dailyBackupDateFormatPattern;
    }

    public void setDailyBackupDateFormatPattern(String dailyBackupDateFormatPattern) {
        this.dailyBackupDateFormatPattern = dailyBackupDateFormatPattern;
    }

    public void setAdminUiProperties(AdminUiProperties adminUiProperties) {
        this.adminUiProperties = adminUiProperties;
    }

    public void setScheduledJobManager(ScheduledJobManager scheduledJobManager) {
        this.scheduledJobManager = scheduledJobManager;
    }

    public boolean getEditMode() {
        return this.editMode;
    }

    public void setConfluenceHome(FilesystemPath confluenceHome) {
        this.confluenceHome = confluenceHome;
    }
}

