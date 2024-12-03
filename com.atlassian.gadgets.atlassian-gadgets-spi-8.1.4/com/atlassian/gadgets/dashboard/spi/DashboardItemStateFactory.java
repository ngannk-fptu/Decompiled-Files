/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.DashboardItemModuleId
 *  com.atlassian.gadgets.DashboardItemState
 *  com.atlassian.gadgets.GadgetState
 *  com.atlassian.gadgets.LocalDashboardItemModuleId
 *  com.atlassian.gadgets.LocalDashboardItemState
 *  com.atlassian.gadgets.OpenSocialDashboardItemModuleId
 *  javax.annotation.Nonnull
 */
package com.atlassian.gadgets.dashboard.spi;

import com.atlassian.gadgets.DashboardItemModuleId;
import com.atlassian.gadgets.DashboardItemState;
import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.LocalDashboardItemModuleId;
import com.atlassian.gadgets.LocalDashboardItemState;
import com.atlassian.gadgets.OpenSocialDashboardItemModuleId;
import javax.annotation.Nonnull;

public interface DashboardItemStateFactory {
    public GadgetState createGadgetState(@Nonnull OpenSocialDashboardItemModuleId var1);

    public DashboardItemState createDashboardItemState(@Nonnull DashboardItemModuleId var1);

    public LocalDashboardItemState createLocalDashboardState(@Nonnull LocalDashboardItemModuleId var1);
}

