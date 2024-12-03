/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.AdminBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BrowseSpaceBreadcrumb;

public class ListPageTemplatesBreadcrumb
extends AbstractBreadcrumb {
    private static final ListPageTemplatesBreadcrumb INSTANCE = new ListPageTemplatesBreadcrumb();
    private Space space;

    private ListPageTemplatesBreadcrumb() {
        super("global.templates", "/pages/templates/listpagetemplates.action");
    }

    ListPageTemplatesBreadcrumb(Space space) {
        super("templates.name", "/pages/templates/listpagetemplates.action?key=" + HtmlUtil.urlEncode(space.getKey()));
        this.space = space;
    }

    @Override
    protected Breadcrumb getParent() {
        if (this.space != null) {
            return new BrowseSpaceBreadcrumb(this.space);
        }
        return AdminBreadcrumb.getInstance();
    }

    public static ListPageTemplatesBreadcrumb getInstance() {
        return INSTANCE;
    }
}

