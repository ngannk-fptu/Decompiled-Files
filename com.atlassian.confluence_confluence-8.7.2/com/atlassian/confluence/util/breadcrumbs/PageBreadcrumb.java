/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb;

public class PageBreadcrumb
extends AbstractBreadcrumb {
    private Page page;

    public PageBreadcrumb(Page page) {
        super(page.getTitle(), page.getUrlPath());
        this.displayTitle = page.getTitle();
        this.page = page;
    }

    @Override
    public Breadcrumb getParent() {
        if (this.page.getParent() != null) {
            return new PageBreadcrumb(this.page.getParent());
        }
        Space space = this.page.getLatestVersion().getSpace();
        return new SpaceBreadcrumb(space);
    }

    public Page getPage() {
        return this.page;
    }
}

