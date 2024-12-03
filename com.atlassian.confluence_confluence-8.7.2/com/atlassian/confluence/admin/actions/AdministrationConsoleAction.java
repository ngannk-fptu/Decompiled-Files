/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.admin.AdminTasklistManager;
import com.atlassian.confluence.admin.tasks.AdminTask;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.event.events.admin.GlobalSettingsViewEvent;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.util.ConfluenceLicenseUtils;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.event.Event;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@AdminOnly
public class AdministrationConsoleAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(AdministrationConsoleAction.class);
    private String displayAlmostSupportPeriodEndMessage;
    private String displaySupportPeriodEndMessage;
    private Date supportPeriodEndDate;
    private Date almostSupportPeriodEndDate;
    private String supportPeriodEndDateString;
    private DateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");
    private BootstrapManager bootstrapManager;
    private LicenseService licenseService;
    private ScheduledJobManager scheduledJobManager;
    private AdminTasklistManager adminTasklistManager;
    private List<AdminTask> adminTasks = null;
    private List<AdminTask> completedTasks = null;
    private List<AdminTask> incompleteTasks = null;
    private HttpContext httpContext;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public String execute() {
        this.setSupportPeriodDates();
        Settings originalSettings = this.settingsManager.getGlobalSettings();
        Settings settings = this.settingsManager.getGlobalSettings();
        if ("off".equals(this.displayAlmostSupportPeriodEndMessage)) {
            settings.setAlmostSupportPeriodEndMessageOff(true);
        } else if (this.checkAlmostSupportPeriodEnd()) {
            this.setDisplayAlmostSupportPeriodEndMessage("on");
        }
        this.settingsManager.updateGlobalSettings(settings);
        this.eventManager.publishEvent((Event)new GlobalSettingsChangedEvent(this, originalSettings, settings, originalSettings.getBaseUrl(), settings.getBaseUrl()));
        this.getAllAdminTasks();
        this.partitionAdminTasks();
        if (this.getActionErrors().size() != 0) {
            return "error";
        }
        return "success";
    }

    @Override
    public String doDefault() {
        this.setSupportPeriodDates();
        if (this.checkAlmostSupportPeriodEnd()) {
            this.setDisplayAlmostSupportPeriodEndMessage("on");
        }
        if (this.checkSupportPeriodEnd()) {
            this.setDisplaySupportPeriodEndMessage("on");
        }
        this.eventManager.publishEvent((Event)new GlobalSettingsViewEvent(this));
        return "input";
    }

    private void setSupportPeriodDates() {
        ConfluenceLicense license = this.licenseService.retrieve();
        this.almostSupportPeriodEndDate = new Date(ConfluenceLicenseUtils.getSupportPeriodAlmostExpiredDate(license));
        this.supportPeriodEndDate = new Date(ConfluenceLicenseUtils.getSupportPeriodEnd(license));
        this.supportPeriodEndDateString = this.dateFormatter.format(this.supportPeriodEndDate);
    }

    private boolean checkAlmostSupportPeriodEnd() {
        return new Date().after(this.almostSupportPeriodEndDate) && new Date().before(this.supportPeriodEndDate) && !this.settingsManager.getGlobalSettings().isAlmostSupportPeriodEndMessageOff();
    }

    private boolean checkSupportPeriodEnd() {
        return new Date().after(this.supportPeriodEndDate);
    }

    public String getDisplayAlmostSupportPeriodEndMessage() {
        return this.displayAlmostSupportPeriodEndMessage;
    }

    public void setDisplayAlmostSupportPeriodEndMessage(String displayAlmostSupportPeriodEndMessage) {
        this.displayAlmostSupportPeriodEndMessage = displayAlmostSupportPeriodEndMessage;
    }

    public String getDisplaySupportPeriodEndMessage() {
        return this.displaySupportPeriodEndMessage;
    }

    public void setDisplaySupportPeriodEndMessage(String displaySupportPeriodEndMessage) {
        this.displaySupportPeriodEndMessage = displaySupportPeriodEndMessage;
    }

    public String getSupportPeriodEndDateString() {
        return this.supportPeriodEndDateString;
    }

    public void setSupportPeriodEndDateString(String supportPeriodEndDateString) {
        this.supportPeriodEndDateString = supportPeriodEndDateString;
    }

    public void setScheduledJobManager(ScheduledJobManager scheduledJobManager) {
        this.scheduledJobManager = scheduledJobManager;
    }

    public void setAdminTasklistManager(AdminTasklistManager adminTasklistManager) {
        this.adminTasklistManager = adminTasklistManager;
    }

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public List<AdminTask> getAllAdminTasks() {
        if (this.adminTasks == null) {
            this.adminTasks = this.adminTasklistManager.getAllTasks();
        }
        return this.adminTasks;
    }

    public List<AdminTask> getIncompleteAdminTasks() {
        if (this.incompleteTasks == null) {
            this.partitionAdminTasks();
        }
        return this.incompleteTasks;
    }

    public List<AdminTask> getCompletedAdminTasks() {
        if (this.completedTasks == null) {
            this.partitionAdminTasks();
        }
        return this.completedTasks;
    }

    public List<String> getAllConfigurationUrisInContext(AdminTask task) {
        String contextPath = this.httpContext.getRequest().getContextPath();
        LinkedList<String> uris = new LinkedList<String>();
        for (String relativeUri : task.getAllConfigurationUris()) {
            uris.add(contextPath + relativeUri);
        }
        return uris;
    }

    private void partitionAdminTasks() {
        this.incompleteTasks = new LinkedList<AdminTask>();
        this.completedTasks = new LinkedList<AdminTask>();
        for (AdminTask entry : this.getAllAdminTasks()) {
            if (entry.getIsCompleted() || entry.isIgnored()) {
                this.completedTasks.add(entry);
                continue;
            }
            this.incompleteTasks.add(entry);
        }
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = (LicenseService)Preconditions.checkNotNull((Object)licenseService);
    }
}

