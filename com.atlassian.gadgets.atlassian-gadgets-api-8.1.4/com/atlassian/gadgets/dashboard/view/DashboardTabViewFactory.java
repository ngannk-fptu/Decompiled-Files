/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.gadgets.dashboard.view;

import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.dashboard.DashboardState;
import com.atlassian.gadgets.dashboard.DashboardTab;
import com.atlassian.gadgets.view.ViewComponent;
import javax.annotation.Nullable;

public interface DashboardTabViewFactory {
    public ViewComponent createDashboardView(Iterable<DashboardTab> var1, DashboardState var2, @Nullable String var3, int var4, GadgetRequestContext var5);
}

