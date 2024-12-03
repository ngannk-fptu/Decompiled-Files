/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork.Action
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.labels.DisplayableLabel;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.AbstractSpaceActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BrowseSpaceBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.DashboardBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb;
import com.opensymphony.xwork.Action;
import java.util.ArrayList;
import java.util.List;

public class LabelBreadcrumb
extends AbstractSpaceActionBreadcrumb {
    private static final SimpleBreadcrumb LABELS_CRUMB = new SimpleBreadcrumb("labels.name", null);

    public LabelBreadcrumb(ConfluenceActionSupport action, DisplayableLabel label, Space space) {
        super(action, space);
        if (label != null) {
            this.title = label.getRealTitle();
            this.displayTitle = label.getRealTitle();
            String spaceKey = space == null ? null : space.getKey();
            this.target = label.getUrlPath(spaceKey);
        }
    }

    @Deprecated(since="8.2")
    public LabelBreadcrumb(com.opensymphony.xwork2.Action action, DisplayableLabel label, Space space) {
        this((ConfluenceActionSupport)action, label, space);
    }

    @Deprecated(since="8.0", forRemoval=true)
    public LabelBreadcrumb(Action action, DisplayableLabel label, Space space) {
        this((ConfluenceActionSupport)action, label, space);
    }

    @Override
    protected List<Breadcrumb> getMyCrumbs() {
        ArrayList<Breadcrumb> crumbs = new ArrayList<Breadcrumb>();
        crumbs.add(LABELS_CRUMB);
        crumbs.add(this);
        return crumbs;
    }

    @Override
    public Breadcrumb getParent() {
        if (this.space != null) {
            return new BrowseSpaceBreadcrumb(this.space);
        }
        return DashboardBreadcrumb.getInstance();
    }
}

