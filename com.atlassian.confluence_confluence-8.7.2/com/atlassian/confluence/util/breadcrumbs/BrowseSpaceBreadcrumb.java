/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb;

public class BrowseSpaceBreadcrumb
extends AbstractBreadcrumb {
    private Space space;

    public BrowseSpaceBreadcrumb(Space space) {
        this.filterTrailingBreadcrumb = false;
        if (space != null) {
            this.title = "browse.space";
            this.target = "/spaces/browsespace.action?key=" + HtmlUtil.urlEncode(space.getKey());
        } else {
            this.title = "space-undefined";
        }
        this.space = space;
    }

    @Override
    protected Breadcrumb getParent() {
        return new SpaceBreadcrumb(this.space);
    }

    public Space getSpace() {
        return this.space;
    }
}

