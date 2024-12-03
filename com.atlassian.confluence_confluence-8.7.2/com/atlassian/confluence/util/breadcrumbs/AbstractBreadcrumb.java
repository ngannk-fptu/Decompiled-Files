/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractBreadcrumb
implements Breadcrumb {
    protected String title;
    protected String target;
    protected String tooltip;
    protected String displayTitle;
    protected String cssClass;
    protected boolean filterTrailingBreadcrumb = true;
    protected static final int MAX_BREADCRUMBS = 8192;

    protected AbstractBreadcrumb() {
    }

    protected AbstractBreadcrumb(String title, String target) {
        this(title, target, null);
    }

    protected AbstractBreadcrumb(String title, String target, String tooltip) {
        this.title = title;
        this.target = target;
        this.tooltip = tooltip;
    }

    @Override
    public String getTarget() {
        return this.target;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getTooltip() {
        return this.tooltip;
    }

    @Override
    public String getDisplayTitle() {
        return this.displayTitle;
    }

    @Override
    public List<Breadcrumb> getBreadcrumbsTrail() {
        LinkedList crumbs = Lists.newLinkedList();
        for (AbstractBreadcrumb parent = this; parent != null; parent = (AbstractBreadcrumb)parent.getParent()) {
            Preconditions.checkArgument((crumbs.size() < 8192 ? 1 : 0) != 0, (Object)("Run over breadcrumb limit for " + this));
            crumbs.addAll(0, parent.getMyCrumbs());
        }
        return crumbs;
    }

    protected List<Breadcrumb> getMyCrumbs() {
        ArrayList<Breadcrumb> crumbs = new ArrayList<Breadcrumb>();
        if (StringUtils.isNotBlank((CharSequence)this.getTitle()) || StringUtils.isNotBlank((CharSequence)this.getDisplayTitle())) {
            crumbs.add(this);
        }
        return crumbs;
    }

    protected abstract Breadcrumb getParent();

    @Override
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    @Override
    public String getCssClass() {
        return this.cssClass;
    }

    public String toString() {
        return this.getTitle();
    }

    @Override
    public void setFilterTrailingBreadcrumb(boolean filterTrailingBreadcrumb) {
        this.filterTrailingBreadcrumb = filterTrailingBreadcrumb;
    }

    @Override
    public boolean filterTrailingBreadcrumb() {
        return this.filterTrailingBreadcrumb;
    }
}

