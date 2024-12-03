/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand;

import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.parameter.ExpandParameter;

public interface ExpandContext<T> {
    public Expandable getExpandable();

    public T getEntity();

    public ExpandParameter getEntityExpandParameter();
}

