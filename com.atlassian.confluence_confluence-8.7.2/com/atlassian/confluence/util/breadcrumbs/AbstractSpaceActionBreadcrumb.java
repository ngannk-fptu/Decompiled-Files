/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork.Action
 *  com.opensymphony.xwork2.Action
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.AbstractActionBreadcrumb;
import com.opensymphony.xwork.Action;

public abstract class AbstractSpaceActionBreadcrumb
extends AbstractActionBreadcrumb {
    protected Space space;

    protected AbstractSpaceActionBreadcrumb(ConfluenceActionSupport action, Space space) {
        super(action);
        this.space = space;
    }

    @Deprecated(since="8.2")
    protected AbstractSpaceActionBreadcrumb(com.opensymphony.xwork2.Action action, Space space) {
        this((ConfluenceActionSupport)action, space);
    }

    @Deprecated(since="8.0", forRemoval=true)
    protected AbstractSpaceActionBreadcrumb(Action action, Space space) {
        this((ConfluenceActionSupport)action, space);
    }
}

