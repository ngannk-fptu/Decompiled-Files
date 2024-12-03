/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.impl.BetterExportSpaceAction;
import com.atlassian.confluence.extra.flyingpdf.impl.ContentTreeLongRunningTask;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.struts2.ServletActionContext;

public class ContentTreeForExportSpaceAction
extends BetterExportSpaceAction {
    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        ContentTreeLongRunningTask contentTreeLongRunningTask = this.pdfExportLongRunningTaskFactory.createNewContentTreeLongRunningTask(this.getI18n(), this.getSpace(), (User)this.getAuthenticatedUser(), this.servletRequest.getContextPath());
        this.taskId = this.longRunningTaskManager.startLongRunningTask((User)this.getAuthenticatedUser(), (LongRunningTask)contentTreeLongRunningTask);
        ServletActionContext.getResponse().setContentType("text/html;charset=UTF-8");
        return "success";
    }
}

