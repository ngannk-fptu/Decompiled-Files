/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Group
 *  com.opensymphony.xwork.Action
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.user.actions.BrowseGroupsAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.breadcrumbs.AbstractActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.AdminBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb;
import com.atlassian.user.Group;
import com.opensymphony.xwork.Action;
import java.util.ArrayList;
import java.util.List;

public class GroupAdminActionBreadcrumb
extends AbstractActionBreadcrumb {
    private static final SimpleBreadcrumb MANAGE_GROUPS_CRUMB = new SimpleBreadcrumb("title.manage.groups", "/admin/users/browsegroups.action");
    private final Group group;

    public GroupAdminActionBreadcrumb(ConfluenceActionSupport action, Group group) {
        super(action);
        this.group = group;
    }

    @Deprecated(since="8.2")
    public GroupAdminActionBreadcrumb(com.opensymphony.xwork2.Action action, Group group) {
        this((ConfluenceActionSupport)action, group);
    }

    @Deprecated(since="8.0", forRemoval=true)
    public GroupAdminActionBreadcrumb(Action action, Group group) {
        this((ConfluenceActionSupport)action, group);
    }

    @Override
    protected List<Breadcrumb> getMyCrumbs() {
        ArrayList<Breadcrumb> crumbs = new ArrayList<Breadcrumb>();
        crumbs.add(MANAGE_GROUPS_CRUMB);
        if (this.group != null) {
            String name = this.group.getName();
            crumbs.add(new SimpleBreadcrumb(name, "/admin/users/domembersofgroupsearch.action?membersOfGroupTerm=" + HtmlUtil.urlEncode(name)));
        }
        if (!(this.action instanceof BrowseGroupsAction)) {
            crumbs.add(this);
        }
        return crumbs;
    }

    @Override
    public Breadcrumb getParent() {
        return AdminBreadcrumb.getInstance();
    }
}

