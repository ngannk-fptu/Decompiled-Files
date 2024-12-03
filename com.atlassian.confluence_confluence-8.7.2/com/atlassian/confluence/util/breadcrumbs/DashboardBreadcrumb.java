/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;

@Deprecated
public class DashboardBreadcrumb
extends AbstractBreadcrumb {
    private static final DashboardBreadcrumb INSTANCE = new DashboardBreadcrumb();

    private DashboardBreadcrumb() {
        super("dashboard.name", "/dashboard.action", "breadcrumbs.dashboard.tooltip");
        this.filterTrailingBreadcrumb = false;
    }

    @Override
    public Breadcrumb getParent() {
        return null;
    }

    public static DashboardBreadcrumb getInstance() {
        return INSTANCE;
    }
}

