/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.factories.PluginFactory
 *  com.atlassian.plugin.loaders.DirectoryPluginLoader
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.plugin.PluginDirectoryProvider;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.factories.PluginFactory;
import com.atlassian.plugin.loaders.DirectoryPluginLoader;
import java.util.List;

public class ConfluenceHomeClassLoadingPluginLoader
extends DirectoryPluginLoader {
    public ConfluenceHomeClassLoadingPluginLoader(PluginDirectoryProvider pluginDirectoryProvider, List<PluginFactory> pluginFactories, PluginEventManager pluginEventManager) {
        super(pluginDirectoryProvider.getPluginDirectory(), pluginFactories, pluginEventManager);
    }
}

