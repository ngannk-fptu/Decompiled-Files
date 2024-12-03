/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.gadgets.dashboard;

import com.atlassian.gadgets.GadgetId;
import com.atlassian.gadgets.dashboard.DashboardId;
import com.atlassian.gadgets.dashboard.DashboardState;
import com.atlassian.gadgets.dashboard.PermissionException;
import io.atlassian.fugue.Option;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface DashboardService {
    public DashboardState get(DashboardId var1, @Nullable String var2) throws PermissionException;

    public DashboardState save(DashboardState var1, @Nullable String var2) throws PermissionException;

    public Option<DashboardState> getDashboardForGadget(GadgetId var1, @Nullable String var2) throws PermissionException;
}

