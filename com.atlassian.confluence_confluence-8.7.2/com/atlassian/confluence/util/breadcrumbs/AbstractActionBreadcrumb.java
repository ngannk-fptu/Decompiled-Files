/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork.Action
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.opensymphony.xwork.Action;

public abstract class AbstractActionBreadcrumb
extends AbstractBreadcrumb {
    protected ConfluenceActionSupport action;

    protected AbstractActionBreadcrumb(ConfluenceActionSupport action) {
        this.action = action;
        this.title = action != null ? action.getClass().getName() + ".action.name" : null;
    }

    @Deprecated(since="8.2")
    protected AbstractActionBreadcrumb(com.opensymphony.xwork2.Action action) {
        this((ConfluenceActionSupport)action);
    }

    @Deprecated(since="8.0", forRemoval=true)
    protected AbstractActionBreadcrumb(Action action) {
        this((ConfluenceActionSupport)action);
    }
}

