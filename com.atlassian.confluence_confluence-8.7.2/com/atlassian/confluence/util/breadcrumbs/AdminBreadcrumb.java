/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.DashboardBreadcrumb;

public class AdminBreadcrumb
extends AbstractBreadcrumb {
    private static final AdminBreadcrumb INSTANCE = new AdminBreadcrumb();

    private AdminBreadcrumb() {
        super("administration.name", "/admin/console.action");
    }

    public static AdminBreadcrumb getInstance() {
        return INSTANCE;
    }

    @Override
    protected Breadcrumb getParent() {
        return DashboardBreadcrumb.getInstance();
    }
}

