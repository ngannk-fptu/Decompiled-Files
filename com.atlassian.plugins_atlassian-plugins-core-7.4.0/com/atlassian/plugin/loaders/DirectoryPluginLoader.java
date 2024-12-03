/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.factories.PluginFactory
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.PluginArtifactFactory;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.factories.PluginFactory;
import com.atlassian.plugin.loaders.DirectoryScanner;
import com.atlassian.plugin.loaders.ScanningPluginLoader;
import java.io.File;
import java.util.List;

public class DirectoryPluginLoader
extends ScanningPluginLoader {
    public DirectoryPluginLoader(File path, List<PluginFactory> pluginFactories, PluginEventManager pluginEventManager) {
        super(new DirectoryScanner(path), pluginFactories, pluginEventManager);
    }

    public DirectoryPluginLoader(File path, List<PluginFactory> pluginFactories, PluginArtifactFactory pluginArtifactFactory, PluginEventManager pluginEventManager) {
        super(new DirectoryScanner(path), pluginFactories, pluginArtifactFactory, pluginEventManager);
    }
}

