/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork.Action
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.admin.actions.mail.ViewMailServersAction;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.util.breadcrumbs.AbstractActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.AdminBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb;
import com.opensymphony.xwork.Action;
import java.util.ArrayList;
import java.util.List;

public class MailServersActionBreadcrumb
extends AbstractActionBreadcrumb {
    private static final SimpleBreadcrumb MAIL_SERVERS_CRUMB = new SimpleBreadcrumb("admin.viewmailservers", "/admin/mail/viewmailservers.action");

    public MailServersActionBreadcrumb(ConfluenceActionSupport action) {
        super(action);
    }

    @Deprecated(since="8.2")
    public MailServersActionBreadcrumb(com.opensymphony.xwork2.Action action) {
        this((ConfluenceActionSupport)action);
    }

    @Deprecated(since="8.0", forRemoval=true)
    public MailServersActionBreadcrumb(Action action) {
        this((ConfluenceActionSupport)action);
    }

    @Override
    protected List<Breadcrumb> getMyCrumbs() {
        ArrayList<Breadcrumb> crumbs = new ArrayList<Breadcrumb>();
        if (!(this.action instanceof ViewMailServersAction)) {
            crumbs.add(MAIL_SERVERS_CRUMB);
        }
        crumbs.add(this);
        return crumbs;
    }

    @Override
    protected Breadcrumb getParent() {
        return AdminBreadcrumb.getInstance();
    }
}

