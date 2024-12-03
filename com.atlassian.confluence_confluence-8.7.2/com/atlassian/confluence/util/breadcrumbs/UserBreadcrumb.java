/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.links.linktypes.UserProfileLink;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.PeopleBreadcrumb;
import com.atlassian.user.User;

public class UserBreadcrumb
extends AbstractBreadcrumb {
    public UserBreadcrumb(User user) {
        super(user.getFullName(), UserProfileLink.getLinkPath(user.getName()));
        this.displayTitle = user.getFullName();
    }

    @Override
    protected Breadcrumb getParent() {
        return PeopleBreadcrumb.getInstance();
    }
}

