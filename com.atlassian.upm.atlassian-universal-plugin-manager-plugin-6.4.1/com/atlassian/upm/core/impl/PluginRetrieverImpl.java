/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.impl;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginFactory;
import com.atlassian.upm.core.PluginRetriever;
import java.util.Objects;

public final class PluginRetrieverImpl
implements PluginRetriever {
    private final UpmPluginAccessor pluginAccessor;
    private final PluginFactory pluginFactory;

    public PluginRetrieverImpl(UpmPluginAccessor pluginAccessor, PluginFactory pluginFactory) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.pluginFactory = Objects.requireNonNull(pluginFactory, "pluginFactory");
    }

    @Override
    public Option<Plugin> getPlugin(String pluginKey) {
        return this.pluginAccessor.getPlugin(pluginKey).map(this.pluginFactory::createPlugin);
    }

    @Override
    public Option<Plugin.Module> getPluginModule(String completeModuleKey) {
        return this.pluginAccessor.getPluginModule(completeModuleKey).map(this.pluginFactory::createModule);
    }

    @Override
    public Iterable<Plugin> getPlugins() {
        return this.pluginFactory.createPlugins(this.pluginAccessor.getPlugins());
    }

    @Override
    public Iterable<Plugin> getPlugins(Iterable<Addon> availablePluginUpdates) {
        return this.pluginFactory.createPlugins(this.pluginAccessor.getPlugins(), availablePluginUpdates);
    }

    @Override
    public boolean isPluginInstalled(String pluginKey) {
        return this.pluginAccessor.isPluginInstalled(pluginKey);
    }

    @Override
    public boolean isPluginEnabled(String pluginKey) {
        return this.pluginAccessor.isPluginEnabled(pluginKey);
    }

    @Override
    public boolean isPluginModuleEnabled(String completeModuleKey) {
        return this.pluginAccessor.isPluginModuleEnabled(completeModuleKey);
    }
}

