/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs.spaceia;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.PagesCollectorBreadcrumb;

public class PageBreadcrumb
extends AbstractBreadcrumb {
    private Page page;

    public PageBreadcrumb(Page page) {
        super(page.getTitle(), page.getUrlPath());
        this.displayTitle = page.getTitle();
        this.page = page;
    }

    @Override
    protected Breadcrumb getParent() {
        if (this.page.getParent() != null) {
            Page parent = this.page.getParent();
            return new PageBreadcrumb(parent);
        }
        Space space = this.page.getLatestVersion().getSpace();
        return new PagesCollectorBreadcrumb(space);
    }
}

