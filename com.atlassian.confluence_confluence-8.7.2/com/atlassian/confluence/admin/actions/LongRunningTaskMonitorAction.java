/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.internal.longrunning.LongRunningTaskManagerInternal;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskUtils;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

public class LongRunningTaskMonitorAction
extends ConfluenceActionSupport {
    private LongRunningTaskManagerInternal longRunningTaskManager;
    private LongRunningTask task;
    private String taskId;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() {
        this.task = this.lookupTask(this.getAuthenticatedUser());
        if (this.task == null) {
            this.taskId = null;
            this.addActionError(this.getText("error.no.task"));
            return "notaskfound";
        }
        if (this.task.getPercentageComplete() == 100) {
            if (this.taskId != null) {
                this.longRunningTaskManager.stopTrackingLongRunningTask(LongRunningTaskId.valueOf(this.taskId));
            } else {
                LongRunningTaskUtils.removeTask();
            }
            return "success";
        }
        return "input";
    }

    private LongRunningTask lookupTask(User user) {
        LongRunningTask theTask = StringUtils.isNotBlank((CharSequence)this.taskId) ? this.longRunningTaskManager.getLongRunningTask(user, LongRunningTaskId.valueOf(this.taskId)) : LongRunningTaskUtils.retrieveTask();
        return theTask;
    }

    @HtmlSafe
    public String getTaskName() {
        return this.task.getName();
    }

    @HtmlSafe
    public String getCurrentStatus() {
        return this.task.getCurrentStatus();
    }

    public LongRunningTask getTask() {
        return this.task;
    }

    public String getPrettyElapsedTime() {
        return this.getTask().getPrettyElapsedTime();
    }

    public String getPrettyTimeRemaining() {
        return this.getTask().getPrettyTimeRemaining();
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doTaskStatus() {
        if (StringUtils.isNotBlank((CharSequence)this.taskId)) {
            this.longRunningTaskManager.startIfQueued(LongRunningTaskId.valueOf(this.taskId));
        }
        this.task = this.lookupTask(this.getAuthenticatedUser());
        if (this.task == null) {
            this.task = this.lookupTask(null);
        }
        ServletActionContext.getResponse().setContentType("text/xml");
        return "success";
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setLongRunningTaskManager(LongRunningTaskManagerInternal longRunningTaskManager) {
        this.longRunningTaskManager = longRunningTaskManager;
    }

    @Override
    public boolean isPermitted() {
        return true;
    }
}

