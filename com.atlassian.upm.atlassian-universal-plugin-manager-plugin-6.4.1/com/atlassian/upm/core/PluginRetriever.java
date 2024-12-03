/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;

public interface PluginRetriever {
    public Option<Plugin> getPlugin(String var1);

    public Option<Plugin.Module> getPluginModule(String var1);

    public Iterable<Plugin> getPlugins();

    public Iterable<Plugin> getPlugins(Iterable<Addon> var1);

    public boolean isPluginInstalled(String var1);

    public boolean isPluginEnabled(String var1);

    public boolean isPluginModuleEnabled(String var1);
}

