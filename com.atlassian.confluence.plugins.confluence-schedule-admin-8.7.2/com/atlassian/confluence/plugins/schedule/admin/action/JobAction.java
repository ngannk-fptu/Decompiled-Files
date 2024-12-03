/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.schedule.managers.ScheduledJobManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.plugins.schedule.admin.action;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class JobAction
extends ConfluenceActionSupport {
    private static final long serialVersionUID = 1L;
    private ScheduledJobManager scheduledJobManager;
    private String id;

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public void setScheduledJobManager(ScheduledJobManager scheduledJobManager) {
        this.scheduledJobManager = scheduledJobManager;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String runJob() throws Exception {
        this.scheduledJobManager.runNow(JobId.of((String)this.id));
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String enableJob() throws Exception {
        this.scheduledJobManager.enable(JobId.of((String)this.id));
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String disableJob() throws Exception {
        this.scheduledJobManager.disable(JobId.of((String)this.id));
        return "success";
    }
}

