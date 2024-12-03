/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.impl;

import com.atlassian.upm.Iterables;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.PluginsEnablementState;
import com.atlassian.upm.core.PluginsEnablementStateAccessor;
import com.atlassian.upm.core.SafeModeAccessor;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SafeModeAccessorImpl
implements SafeModeAccessor {
    protected final UpmPluginAccessor pluginAccessor;
    private final PluginRetriever pluginRetriever;
    protected final PluginMetadataAccessor metadata;
    private final PluginsEnablementStateAccessor enablementStateAccessor;

    public SafeModeAccessorImpl(UpmPluginAccessor pluginAccessor, PluginRetriever pluginRetriever, PluginMetadataAccessor metadata, PluginsEnablementStateAccessor enablementStateAccessor) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.enablementStateAccessor = Objects.requireNonNull(enablementStateAccessor, "enablementStateAccessor");
    }

    @Override
    public boolean isSafeMode() {
        return this.enablementStateAccessor.hasSavedConfiguration();
    }

    @Override
    public PluginsEnablementState getCurrentConfiguration() {
        return this.getCurrentConfiguration(Iterables.toList(this.pluginRetriever.getPlugins()));
    }

    protected PluginsEnablementState getCurrentConfiguration(List<Plugin> plugins) {
        return new PluginsEnablementState.Builder(this.transformPluginToPluginConfigurations(plugins), this.pluginRetriever, this.metadata).title("Current Configuration").build();
    }

    protected List<PluginsEnablementState.PluginState> transformPluginToPluginConfigurations(List<Plugin> plugins) {
        return plugins.stream().map(plugin -> new PluginsEnablementState.PluginState.Builder((Plugin)plugin, this.pluginAccessor).build()).collect(Collectors.toList());
    }
}

