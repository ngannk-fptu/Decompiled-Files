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
import com.atlassian.confluence.util.breadcrumbs.AbstractActionBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.ListPageTemplatesBreadcrumb;
import com.opensymphony.xwork.Action;

public class PageTemplatesActionBreadcrumb
extends AbstractActionBreadcrumb {
    private final Space space;

    public PageTemplatesActionBreadcrumb(ConfluenceActionSupport action, Space space) {
        super(action);
        this.space = space;
    }

    @Deprecated(since="8.2")
    public PageTemplatesActionBreadcrumb(com.opensymphony.xwork2.Action action, Space space) {
        this((ConfluenceActionSupport)action, space);
    }

    @Deprecated(since="8.0", forRemoval=true)
    public PageTemplatesActionBreadcrumb(Action action, Space space) {
        this((ConfluenceActionSupport)action, space);
    }

    @Override
    public Breadcrumb getParent() {
        if (this.space != null) {
            return new ListPageTemplatesBreadcrumb(this.space);
        }
        return ListPageTemplatesBreadcrumb.getInstance();
    }
}

