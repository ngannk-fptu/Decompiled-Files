/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs.spaceia;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;

public class BlogCollectorBreadcrumb
extends AbstractBreadcrumb {
    public BlogCollectorBreadcrumb(Space space) {
        super("breadcrumbs.collector.blog", "/pages/viewrecentblogposts.action?key=" + HtmlUtil.urlEncode(space.getKey()));
        this.filterTrailingBreadcrumb = false;
    }

    @Override
    protected Breadcrumb getParent() {
        return null;
    }
}

