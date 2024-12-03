/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;

public abstract class AbstractSpaceBreadcrumb
extends AbstractBreadcrumb {
    protected Space space;

    public AbstractSpaceBreadcrumb(Space space) {
        this.space = space;
    }
}

