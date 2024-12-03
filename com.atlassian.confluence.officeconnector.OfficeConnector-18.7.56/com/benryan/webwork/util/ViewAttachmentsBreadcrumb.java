/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BlogPostBreadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.PageBreadcrumb
 */
package com.benryan.webwork.util;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BlogPostBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.PageBreadcrumb;

public class ViewAttachmentsBreadcrumb
extends AbstractBreadcrumb {
    AbstractPage page;

    public ViewAttachmentsBreadcrumb(AbstractPage page) {
        super("com.atlassian.confluence.pages.actions.ViewPageAttachmentsAction.action.name", page.getAttachmentsUrlPath());
        this.page = page;
    }

    protected Breadcrumb getParent() {
        if (this.page instanceof BlogPost) {
            return new BlogPostBreadcrumb((BlogPost)this.page);
        }
        return new PageBreadcrumb((Page)this.page);
    }
}

