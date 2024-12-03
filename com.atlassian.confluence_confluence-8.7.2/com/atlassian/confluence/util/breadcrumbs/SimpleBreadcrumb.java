/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;

public class SimpleBreadcrumb
extends AbstractBreadcrumb {
    private final Breadcrumb parent;

    public SimpleBreadcrumb(String i18nKey, String path) {
        super(i18nKey, path);
        this.parent = null;
    }

    public SimpleBreadcrumb(String i18nKey, String path, Breadcrumb parent) {
        super(i18nKey, path);
        this.parent = parent;
    }

    @Override
    protected Breadcrumb getParent() {
        return this.parent;
    }
}

