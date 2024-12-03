/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.dashboard.DashboardId
 *  javax.annotation.Nullable
 */
package com.atlassian.gadgets.dashboard.spi;

import com.atlassian.gadgets.dashboard.DashboardId;
import javax.annotation.Nullable;

public interface DashboardPermissionService {
    public boolean isReadableBy(DashboardId var1, @Nullable String var2);

    public boolean isWritableBy(DashboardId var1, @Nullable String var2);
}

