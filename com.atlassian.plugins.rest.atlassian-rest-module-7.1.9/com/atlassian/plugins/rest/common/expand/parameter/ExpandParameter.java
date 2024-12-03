/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand.parameter;

import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.parameter.Indexes;

public interface ExpandParameter {
    public boolean shouldExpand(Expandable var1);

    public Indexes getIndexes(Expandable var1);

    public ExpandParameter getExpandParameter(Expandable var1);

    public boolean isEmpty();
}

