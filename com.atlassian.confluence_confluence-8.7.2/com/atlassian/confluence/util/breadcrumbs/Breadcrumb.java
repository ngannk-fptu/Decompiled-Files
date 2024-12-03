/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import java.util.List;

public interface Breadcrumb {
    public String getTarget();

    public String getTitle();

    public String getDisplayTitle();

    public String getTooltip();

    public List<Breadcrumb> getBreadcrumbsTrail();

    public String getCssClass();

    public void setCssClass(String var1);

    public void setFilterTrailingBreadcrumb(boolean var1);

    public boolean filterTrailingBreadcrumb();
}

