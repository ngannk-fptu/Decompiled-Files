/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.opensymphony.xwork.Action
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.user.actions.SearchUsersAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.breadcrumbs.AbstractActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.AdminBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb;
import com.atlassian.user.User;
import com.opensymphony.xwork.Action;
import java.util.ArrayList;
import java.util.List;

public class UserAdminActionBreadcrumb
extends AbstractActionBreadcrumb {
    private static final Breadcrumb SEARCH_USERS_CRUMB = new SimpleBreadcrumb("users.name", "/admin/users/browseusers.action");
    private final User user;

    public UserAdminActionBreadcrumb(ConfluenceActionSupport action, User user) {
        super(action);
        this.user = user;
    }

    @Deprecated(since="8.2")
    public UserAdminActionBreadcrumb(com.opensymphony.xwork2.Action action, User user) {
        this((ConfluenceActionSupport)action, user);
    }

    @Deprecated(since="8.0", forRemoval=true)
    public UserAdminActionBreadcrumb(Action action, User user) {
        this((ConfluenceActionSupport)action, user);
    }

    @Override
    protected List<Breadcrumb> getMyCrumbs() {
        ArrayList<Breadcrumb> crumbs = new ArrayList<Breadcrumb>();
        if (!(this.action instanceof SearchUsersAction)) {
            crumbs.add(SEARCH_USERS_CRUMB);
        }
        if (this.user != null) {
            String name = this.user.getName();
            crumbs.add(new SimpleBreadcrumb(name, "/admin/users/viewuser.action?username=" + HtmlUtil.urlEncode(name)));
        }
        crumbs.add(this);
        return crumbs;
    }

    @Override
    public Breadcrumb getParent() {
        return AdminBreadcrumb.getInstance();
    }
}

