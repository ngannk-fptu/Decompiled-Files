/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb;
import java.util.List;

public class BlogPostDateBreadcrumb
extends AbstractBreadcrumb {
    private Space space;
    private String year;
    private String monthNumeric;
    private String monthText;
    private String day;

    public BlogPostDateBreadcrumb(Space space, String year, String monthNumeric, String monthText, String day) {
        super(null, null);
        this.space = space;
        this.year = year;
        this.monthNumeric = monthNumeric;
        this.monthText = monthText;
        this.day = day;
    }

    protected BlogPostDateBreadcrumb(String title, String target) {
        super(title, target);
    }

    @Override
    protected List<Breadcrumb> getMyCrumbs() {
        com.atlassian.confluence.util.breadcrumbs.spaceia.BlogPostDateBreadcrumb blogPostDateBreadcrumb = new com.atlassian.confluence.util.breadcrumbs.spaceia.BlogPostDateBreadcrumb(this.space, this.year, this.monthNumeric, this.monthText, this.day);
        return blogPostDateBreadcrumb.getMyCrumbs();
    }

    @Override
    public Breadcrumb getParent() {
        return new SpaceBreadcrumb(this.space);
    }
}

