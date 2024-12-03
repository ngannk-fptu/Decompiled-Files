/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.factories.PluginFactory
 *  com.atlassian.plugin.loaders.BundledPluginLoader
 *  com.atlassian.plugin.loaders.PluginLoader
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.plugin.PluginDirectoryProvider;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.factories.PluginFactory;
import com.atlassian.plugin.loaders.BundledPluginLoader;
import com.atlassian.plugin.loaders.PluginLoader;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

public class ExtraBundledPluginsFactory {
    private final PluginDirectoryProvider pluginDirectoryProvider;
    private final List<PluginFactory> pluginFactories;
    private final PluginEventManager eventManager;

    public ExtraBundledPluginsFactory(PluginDirectoryProvider pluginDirectoryProvider, List<PluginFactory> pluginFactories, PluginEventManager eventManager) {
        this.pluginDirectoryProvider = pluginDirectoryProvider;
        this.pluginFactories = pluginFactories;
        this.eventManager = eventManager;
    }

    public PluginLoader newPluginLoader(File location) {
        try {
            return new BundledPluginLoader(location.toURI().toURL(), this.pluginDirectoryProvider.getBundledPluginDirectory(), this.pluginFactories, this.eventManager);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Unable to create well-formed URL from file " + location + " " + e.getMessage(), e);
        }
    }
}

