/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork.Action
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAdminAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.breadcrumbs.AbstractSpaceActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb;
import com.opensymphony.xwork.Action;
import java.util.ArrayList;
import java.util.List;

public class SpaceAdminActionBreadcrumb
extends AbstractSpaceActionBreadcrumb {
    public SpaceAdminActionBreadcrumb(ConfluenceActionSupport action, Space space) {
        super(action, space);
    }

    @Deprecated(since="8.2")
    public SpaceAdminActionBreadcrumb(com.opensymphony.xwork2.Action action, Space space) {
        this((ConfluenceActionSupport)action, space);
    }

    @Deprecated(since="8.0", forRemoval=true)
    public SpaceAdminActionBreadcrumb(Action action, Space space) {
        this((ConfluenceActionSupport)action, space);
    }

    @Override
    protected List<Breadcrumb> getMyCrumbs() {
        ArrayList<Breadcrumb> crumbs = new ArrayList<Breadcrumb>();
        if (this.space != null && !(this.action instanceof SpaceAdminAction)) {
            crumbs.add(new SimpleBreadcrumb("breadcrumbs.space.admin", "/spaces/spaceadmin.action?key=" + HtmlUtil.urlEncode(this.space.getKey())));
        }
        crumbs.add(this);
        return crumbs;
    }

    @Override
    protected Breadcrumb getParent() {
        return new SpaceBreadcrumb(this.space);
    }

    public Space getSpace() {
        return this.space;
    }
}

