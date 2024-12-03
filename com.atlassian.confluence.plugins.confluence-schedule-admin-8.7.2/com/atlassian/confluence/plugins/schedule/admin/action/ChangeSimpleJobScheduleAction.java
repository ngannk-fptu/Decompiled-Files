/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.Beanable
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.schedule.managers.ScheduledJobManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.schedule.admin.action;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;

public class ChangeSimpleJobScheduleAction
extends ConfluenceActionSupport
implements Beanable {
    private ScheduledJobManager scheduledJobManager;
    private String id;
    private String repeatInterval;
    private long repeatIntervalValue;
    private Result result;

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public String execute() throws Exception {
        this.result = this.validateInput();
        if (this.result.getError() != null) {
            return "error";
        }
        this.scheduledJobManager.updateSimpleJobSchedule(JobId.of((String)this.id), this.repeatIntervalValue * 1000L);
        return "success";
    }

    public Object getBean() {
        return this.result;
    }

    public void setScheduledJobManager(ScheduledJobManager scheduledJobManager) {
        this.scheduledJobManager = scheduledJobManager;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRepeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    private Result validateInput() {
        if (StringUtils.isEmpty((CharSequence)this.id)) {
            return new Result(this.getText("scheduledjob.error.id.missing"));
        }
        try {
            this.repeatIntervalValue = Long.parseLong(this.repeatInterval);
            if (this.repeatIntervalValue < 0L) {
                return new Result(this.getText("scheduledjob.error.repeatInterval.invalid"));
            }
        }
        catch (Exception e) {
            return new Result(this.getText("scheduledjob.error.repeatInterval.invalid"));
        }
        return new Result();
    }

    public static class Result {
        private final String error;

        private Result(String error) {
            this.error = error;
        }

        private Result() {
            this(null);
        }

        public String getError() {
            return this.error;
        }
    }
}

