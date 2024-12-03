/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  io.atlassian.fugue.Option
 */
package com.atlassian.gadgets.plugins;

import com.atlassian.gadgets.plugins.DashboardItemModule;
import com.atlassian.plugin.ModuleDescriptor;
import io.atlassian.fugue.Option;

public interface DashboardItemModuleDescriptor
extends ModuleDescriptor<DashboardItemModule> {
    public Option<String> getGadgetSpecUriToReplace();

    public Option<DashboardItemModule.DirectoryDefinition> getDirectoryDefinition();
}

