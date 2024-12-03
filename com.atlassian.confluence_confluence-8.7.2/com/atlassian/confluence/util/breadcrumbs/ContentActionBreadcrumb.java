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
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.AbstractCreateAndEditPageAction;
import com.atlassian.confluence.pages.actions.AbstractPageAction;
import com.atlassian.confluence.pages.actions.RevertPageBackToVersionAction;
import com.atlassian.confluence.pages.actions.ViewPageAction;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.AbstractSpaceActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BlogPostBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.DashboardBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.LabelBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.PageBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb;
import com.opensymphony.xwork.Action;
import java.util.ArrayList;
import java.util.List;

public class ContentActionBreadcrumb
extends AbstractSpaceActionBreadcrumb {
    private AbstractPage page;
    private final DisplayableLabel label;
    private final Breadcrumb parentActionBreadcrumb;

    public ContentActionBreadcrumb(ConfluenceActionSupport action, Space space, AbstractPage page, DisplayableLabel label, Breadcrumb spaceOperationsBreadcrumb) {
        super(action, space);
        this.page = page;
        this.label = label;
        this.parentActionBreadcrumb = this.getPreviousActionBreadcrumb(spaceOperationsBreadcrumb);
    }

    @Deprecated(since="8.2")
    public ContentActionBreadcrumb(com.opensymphony.xwork2.Action action, Space space, AbstractPage page, DisplayableLabel label, Breadcrumb spaceOperationsBreadcrumb) {
        this((ConfluenceActionSupport)action, space, page, label, spaceOperationsBreadcrumb);
    }

    @Deprecated(since="8.0", forRemoval=true)
    public ContentActionBreadcrumb(Action action, Space space, AbstractPage page, DisplayableLabel label, Breadcrumb spaceOperationsBreadcrumb) {
        this((ConfluenceActionSupport)action, space, page, label, spaceOperationsBreadcrumb);
    }

    @Override
    protected List<Breadcrumb> getMyCrumbs() {
        ArrayList<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
        if (this.label == null) {
            if (this.parentActionBreadcrumb != null) {
                breadcrumbs.add(this.parentActionBreadcrumb);
            }
            if (!(this.action instanceof ViewPageAction) && !(this.action instanceof AbstractCreateAndEditPageAction)) {
                breadcrumbs.add(this);
            }
        }
        return breadcrumbs;
    }

    @Override
    public Breadcrumb getParent() {
        if (this.label != null) {
            return new LabelBreadcrumb(this.action, this.label, this.space);
        }
        if (this.page != null) {
            if (this.page instanceof Page) {
                return new PageBreadcrumb((Page)this.page);
            }
            if (this.page instanceof BlogPost) {
                return new BlogPostBreadcrumb((BlogPost)this.page);
            }
        } else if (this.space != null) {
            return this.parentActionBreadcrumb instanceof AbstractBreadcrumb ? ((AbstractBreadcrumb)this.parentActionBreadcrumb).getParent() : new SpaceBreadcrumb(this.space);
        }
        return DashboardBreadcrumb.getInstance();
    }

    private Breadcrumb getPreviousActionBreadcrumb(Breadcrumb spaceOperationsBreadcrumb) {
        if (this.space != null && (this.action instanceof AbstractSpaceAction || this.action instanceof SpaceAware)) {
            return spaceOperationsBreadcrumb;
        }
        if (this.action instanceof RevertPageBackToVersionAction) {
            AbstractPageAction abstractPageAction = (AbstractPageAction)this.action;
            this.page = abstractPageAction.getPage();
            return new SimpleBreadcrumb("information.name", "/pages/viewinfo.action?pageId=" + this.page.getIdAsString());
        }
        return null;
    }
}

