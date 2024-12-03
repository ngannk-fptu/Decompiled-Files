/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util.breadcrumbs.spaceia;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.BlogCollectorBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.BlogCrumb;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class BlogPostDateBreadcrumb
extends AbstractBreadcrumb {
    private final Space space;
    private final String year;
    private final String monthNumeric;
    private final String monthText;
    private final String day;
    private Breadcrumb parent;

    public BlogPostDateBreadcrumb(Space space, String year, String monthNumeric, String monthText, String day) {
        this.space = space;
        this.year = year;
        this.monthNumeric = monthNumeric;
        this.monthText = monthText;
        this.day = day;
    }

    public BlogPostDateBreadcrumb(Space space, String year, String monthNumeric, String monthText, String day, Breadcrumb parent) {
        this(space, year, monthNumeric, monthText, day);
        this.parent = parent;
    }

    @Override
    public List<Breadcrumb> getMyCrumbs() {
        ArrayList<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>(4);
        if (this.space != null) {
            breadcrumbs.add(new BlogCrumb(this.year, null));
            String targetBase = "/display/" + this.space.getKey() + "/";
            String monthTarget = targetBase + this.year + "/" + this.monthNumeric;
            breadcrumbs.add(new BlogCrumb(this.monthText, monthTarget));
            if (StringUtils.isNotBlank((CharSequence)this.day)) {
                breadcrumbs.add(new BlogCrumb(this.day, null));
            }
        }
        return breadcrumbs;
    }

    @Override
    public Breadcrumb getParent() {
        if (this.parent != null) {
            return this.parent;
        }
        return new BlogCollectorBreadcrumb(this.space);
    }
}

