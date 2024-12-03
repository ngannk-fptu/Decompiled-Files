/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import java.util.List;

public class CompositeBreadcrumb
extends AbstractBreadcrumb {
    private final List<Breadcrumb> originalTrail;
    private final Breadcrumb topmostParent;

    public CompositeBreadcrumb(List<Breadcrumb> originalTrail, Breadcrumb topmostParent) {
        this.originalTrail = originalTrail;
        this.topmostParent = topmostParent;
    }

    @Override
    protected Breadcrumb getParent() {
        return this.topmostParent;
    }

    @Override
    protected List<Breadcrumb> getMyCrumbs() {
        return this.originalTrail;
    }

    @Override
    public void setFilterTrailingBreadcrumb(boolean filterTrailingBreadcrumb) {
        if (!this.originalTrail.isEmpty()) {
            this.originalTrail.get(this.originalTrail.size() - 1).setFilterTrailingBreadcrumb(filterTrailingBreadcrumb);
        } else {
            this.topmostParent.setFilterTrailingBreadcrumb(filterTrailingBreadcrumb);
        }
    }
}

