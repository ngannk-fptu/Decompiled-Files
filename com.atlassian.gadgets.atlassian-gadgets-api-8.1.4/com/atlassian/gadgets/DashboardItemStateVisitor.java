/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.LocalDashboardItemState;

public interface DashboardItemStateVisitor<T> {
    public T visit(GadgetState var1);

    public T visit(LocalDashboardItemState var1);
}

