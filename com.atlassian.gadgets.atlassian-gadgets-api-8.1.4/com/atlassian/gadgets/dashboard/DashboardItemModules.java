/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 */
package com.atlassian.gadgets.dashboard;

import com.atlassian.gadgets.plugins.DashboardItemModuleDescriptor;
import com.atlassian.plugin.ModuleCompleteKey;
import io.atlassian.fugue.Option;
import javax.annotation.Nonnull;

public interface DashboardItemModules {
    public Option<DashboardItemModuleDescriptor> getDashboardItemModuleDescriptor(@Nonnull String var1);

    public Iterable<DashboardItemModuleDescriptor> getDashboardItemsWithDirectoryDefinition();

    public Option<DashboardItemModuleDescriptor> getDashboardItemForModuleKey(@Nonnull ModuleCompleteKey var1);
}

