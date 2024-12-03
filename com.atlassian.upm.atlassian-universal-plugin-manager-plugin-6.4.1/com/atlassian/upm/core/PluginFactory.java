/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.core;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.upm.core.Plugin;

public interface PluginFactory {
    public Plugin createPlugin(com.atlassian.plugin.Plugin var1);

    public Iterable<Plugin> createPlugins(Iterable<com.atlassian.plugin.Plugin> var1);

    public Iterable<Plugin> createPlugins(Iterable<com.atlassian.plugin.Plugin> var1, Iterable<Addon> var2);

    public Plugin.Module createModule(ModuleDescriptor<?> var1);

    public Plugin.Module createModule(ModuleDescriptor<?> var1, Plugin var2);
}

