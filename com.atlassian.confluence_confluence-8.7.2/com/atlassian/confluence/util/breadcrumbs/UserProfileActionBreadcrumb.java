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
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.confluence.util.breadcrumbs.AbstractActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.PeopleBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.UserBreadcrumb;
import com.atlassian.user.User;
import com.opensymphony.xwork.Action;
import java.util.ArrayList;
import java.util.List;

public class UserProfileActionBreadcrumb
extends AbstractActionBreadcrumb {
    public UserProfileActionBreadcrumb(ConfluenceActionSupport action) {
        super(action);
    }

    @Deprecated(since="8.2")
    public UserProfileActionBreadcrumb(com.opensymphony.xwork2.Action action) {
        this((ConfluenceActionSupport)action);
    }

    @Deprecated(since="8.0", forRemoval=true)
    public UserProfileActionBreadcrumb(Action action) {
        this((ConfluenceActionSupport)action);
    }

    @Override
    protected List<Breadcrumb> getMyCrumbs() {
        ArrayList<Breadcrumb> crumbs = new ArrayList<Breadcrumb>();
        User user = ((UserAware)((Object)this.action)).getUser();
        crumbs.add(new UserBreadcrumb(user));
        return crumbs;
    }

    @Override
    protected Breadcrumb getParent() {
        return PeopleBreadcrumb.getInstance();
    }
}

