/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.upm.api.util.Option;

public interface UpmPluginAccessor {
    public Option<Plugin> getPlugin(String var1);

    public Iterable<Plugin> getPlugins();

    public boolean isPluginInstalled(String var1);

    public boolean isPluginEnabled(String var1);

    public Option<ModuleDescriptor<?>> getPluginModule(String var1);

    public boolean isPluginModuleEnabled(String var1);
}

