/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.LocalDashboardItemModuleId;
import com.atlassian.gadgets.OpenSocialDashboardItemModuleId;

public interface DashboardItemModuleIdVisitor<T> {
    public T visit(OpenSocialDashboardItemModuleId var1);

    public T visit(LocalDashboardItemModuleId var1);
}

