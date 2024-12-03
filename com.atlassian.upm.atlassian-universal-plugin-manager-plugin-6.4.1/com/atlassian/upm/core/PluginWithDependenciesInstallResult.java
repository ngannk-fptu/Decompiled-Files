/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

import com.atlassian.upm.Iterables;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginFactory;
import com.atlassian.upm.spi.PluginInstallResult;
import java.util.Collections;
import java.util.stream.Collectors;

public class PluginWithDependenciesInstallResult {
    private final Plugin plugin;
    private final Iterable<Plugin> dependencies;

    private PluginWithDependenciesInstallResult(PluginInstallResult result, PluginFactory pluginFactory) {
        this.plugin = pluginFactory.createPlugin(result.getPlugin());
        this.dependencies = Collections.unmodifiableList(Iterables.toStream(result.getDependencies()).map(pluginFactory::createPlugin).collect(Collectors.toList()));
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Iterable<Plugin> getDependencies() {
        return this.dependencies;
    }

    public static PluginWithDependenciesInstallResult from(PluginInstallResult result, PluginFactory pluginFactory) {
        return new PluginWithDependenciesInstallResult(result, pluginFactory);
    }
}

