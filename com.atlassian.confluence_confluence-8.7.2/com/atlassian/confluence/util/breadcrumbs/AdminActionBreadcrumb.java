/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork.Action
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.admin.actions.AdministrationConsoleAction;
import com.atlassian.confluence.admin.actions.LongRunningTaskMonitorAction;
import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.util.breadcrumbs.AbstractActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.AdminBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.DashboardBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb;
import com.opensymphony.xwork.Action;
import java.util.ArrayList;
import java.util.List;

public class AdminActionBreadcrumb
extends AbstractActionBreadcrumb {
    public AdminActionBreadcrumb(ConfluenceActionSupport action) {
        super(action);
    }

    @Deprecated(since="8.2")
    public AdminActionBreadcrumb(com.opensymphony.xwork2.Action action) {
        this((ConfluenceActionSupport)action);
    }

    @Deprecated(since="8.0", forRemoval=true)
    public AdminActionBreadcrumb(Action action) {
        this((ConfluenceActionSupport)action);
    }

    @Override
    protected List<Breadcrumb> getMyCrumbs() {
        ArrayList<Breadcrumb> crumbs = new ArrayList<Breadcrumb>();
        if (!(this.action instanceof AdministrationConsoleAction)) {
            crumbs.add(AdminBreadcrumb.getInstance());
        }
        if (this.action instanceof LongRunningTaskMonitorAction) {
            crumbs.add(new SimpleBreadcrumb("running.task", "/admin/longrunningtask.action"));
        } else if (this.action instanceof LookAndFeel) {
            crumbs.add(new SimpleBreadcrumb("lookandfeel.name", "/admin/lookandfeel.action"));
        }
        crumbs.add(this);
        return crumbs;
    }

    @Override
    public Breadcrumb getParent() {
        return DashboardBreadcrumb.getInstance();
    }
}

