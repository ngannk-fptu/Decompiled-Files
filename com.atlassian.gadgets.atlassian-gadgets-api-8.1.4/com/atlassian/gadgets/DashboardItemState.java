/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.DashboardItemStateVisitor;
import com.atlassian.gadgets.GadgetId;
import com.atlassian.gadgets.dashboard.Color;

public interface DashboardItemState {
    public GadgetId getId();

    public Color getColor();

    public <V> V accept(DashboardItemStateVisitor<V> var1);
}

