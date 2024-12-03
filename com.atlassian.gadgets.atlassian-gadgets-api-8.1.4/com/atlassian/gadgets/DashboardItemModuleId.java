/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.DashboardItemModuleIdVisitor;
import com.atlassian.gadgets.DashboardItemType;

public interface DashboardItemModuleId {
    public String getId();

    public DashboardItemType getType();

    public <T> T accept(DashboardItemModuleIdVisitor<T> var1);
}

