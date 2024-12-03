/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetId
 *  com.atlassian.gadgets.dashboard.DashboardId
 *  com.atlassian.gadgets.dashboard.DashboardNotFoundException
 *  com.atlassian.gadgets.dashboard.DashboardState
 *  javax.annotation.Nonnull
 */
package com.atlassian.gadgets.dashboard.spi;

import com.atlassian.gadgets.GadgetId;
import com.atlassian.gadgets.dashboard.DashboardId;
import com.atlassian.gadgets.dashboard.DashboardNotFoundException;
import com.atlassian.gadgets.dashboard.DashboardState;
import com.atlassian.gadgets.dashboard.spi.DashboardStateStoreException;
import com.atlassian.gadgets.dashboard.spi.changes.DashboardChange;
import javax.annotation.Nonnull;

public interface DashboardStateStore {
    public DashboardState retrieve(@Nonnull DashboardId var1) throws DashboardNotFoundException, DashboardStateStoreException;

    public DashboardState update(@Nonnull DashboardState var1, @Nonnull Iterable<DashboardChange> var2) throws DashboardStateStoreException;

    public void remove(@Nonnull DashboardId var1) throws DashboardStateStoreException;

    public DashboardState findDashboardWithGadget(@Nonnull GadgetId var1) throws DashboardNotFoundException;
}

