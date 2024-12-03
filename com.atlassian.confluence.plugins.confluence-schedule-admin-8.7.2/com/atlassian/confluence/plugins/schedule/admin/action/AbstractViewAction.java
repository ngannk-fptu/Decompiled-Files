/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.schedule.managers.ScheduledJobManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.schedule.admin.action;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;
import java.text.DateFormat;
import java.util.Date;

public abstract class AbstractViewAction
extends ConfluenceActionSupport {
    private static final long serialVersionUID = 1L;
    protected ScheduledJobManager scheduledJobManager;

    public void setScheduledJobManager(ScheduledJobManager scheduledJobManager) {
        this.scheduledJobManager = scheduledJobManager;
    }

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        return this.getDateFormatter().formatDateTime(date);
    }

    public String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DateFormat.getDateInstance(2, this.getLocale()).format(date);
    }

    public String formatTime(Date date) {
        if (date == null) {
            return "";
        }
        return DateFormat.getTimeInstance(2, this.getLocale()).format(date);
    }
}

