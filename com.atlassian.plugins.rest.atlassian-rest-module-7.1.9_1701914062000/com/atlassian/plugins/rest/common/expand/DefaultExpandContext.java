/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand;

import com.atlassian.plugins.rest.common.expand.ExpandContext;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.parameter.ExpandParameter;

public class DefaultExpandContext<T>
implements ExpandContext<T> {
    private final T entity;
    private final Expandable expandable;
    private final ExpandParameter expandParameter;

    public DefaultExpandContext(T entity, Expandable expandable, ExpandParameter expandParameter) {
        this.expandable = expandable;
        this.entity = entity;
        this.expandParameter = expandParameter;
    }

    @Override
    public Expandable getExpandable() {
        return this.expandable;
    }

    @Override
    public T getEntity() {
        return this.entity;
    }

    @Override
    public ExpandParameter getEntityExpandParameter() {
        return this.expandParameter;
    }
}

