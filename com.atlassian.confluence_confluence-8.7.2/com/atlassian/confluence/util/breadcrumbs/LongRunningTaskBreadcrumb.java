/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.admin.actions.LongRunningTaskMonitorAction;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.opensymphony.xwork2.Action;

public class LongRunningTaskBreadcrumb
extends AbstractBreadcrumb {
    private static final SimpleBreadcrumb PARENT = new SimpleBreadcrumb("running.task", "/admin/longrunningtask.action");

    public LongRunningTaskBreadcrumb(Action action) {
        LongRunningTaskMonitorAction longAction = (LongRunningTaskMonitorAction)action;
        LongRunningTask task = longAction.getTask();
        this.title = task == null ? "task.not.found" : task.getNameKey();
    }

    @Override
    protected Breadcrumb getParent() {
        return PARENT;
    }
}

