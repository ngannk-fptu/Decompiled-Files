/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.DashboardBreadcrumb;

public class PeopleBreadcrumb
extends AbstractBreadcrumb {
    private static final PeopleBreadcrumb INSTANCE = new PeopleBreadcrumb();

    private PeopleBreadcrumb() {
        super("people.directory.name", "/peopledirectory.action");
    }

    public static PeopleBreadcrumb getInstance() {
        return INSTANCE;
    }

    @Override
    protected Breadcrumb getParent() {
        return DashboardBreadcrumb.getInstance();
    }
}

