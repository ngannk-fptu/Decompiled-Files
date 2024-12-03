/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.upm.impl;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.UpmPluginAccessor;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.spi.PluginControlHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UpmPluginAccessorImpl
implements UpmPluginAccessor {
    private final PluginAccessor pluginAccessor;
    private final PluginControlHandlerRegistry pluginControllerHandlerRegistry;

    public UpmPluginAccessorImpl(PluginAccessor pluginAccessor, PluginControlHandlerRegistry pluginControllerHandlerRegistry) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.pluginControllerHandlerRegistry = Objects.requireNonNull(pluginControllerHandlerRegistry, "pluginControllerHanlderRegistry");
    }

    @Override
    public Option<Plugin> getPlugin(String pluginKey) {
        for (PluginControlHandler handler : this.getControlHandlers()) {
            if (!handler.canControl(pluginKey)) continue;
            return Option.some(handler.getPlugin(pluginKey));
        }
        return Option.option(this.pluginAccessor.getPlugin(pluginKey));
    }

    public List<Plugin> getPlugins() {
        ArrayList<? extends Plugin> plugins = new ArrayList<Plugin>();
        plugins.addAll(this.pluginAccessor.getPlugins());
        for (PluginControlHandler handler : this.getControlHandlers()) {
            plugins.addAll(handler.getPlugins());
        }
        return Collections.unmodifiableList(plugins);
    }

    @Override
    public boolean isPluginInstalled(String pluginKey) {
        return this.getPlugin(pluginKey).isDefined();
    }

    @Override
    public boolean isPluginEnabled(String pluginKey) {
        for (PluginControlHandler handler : this.getControlHandlers()) {
            if (!handler.canControl(pluginKey)) continue;
            return handler.isPluginEnabled(pluginKey);
        }
        return this.pluginAccessor.isPluginEnabled(pluginKey);
    }

    private Iterable<PluginControlHandler> getControlHandlers() {
        return this.pluginControllerHandlerRegistry.getHandlers();
    }

    @Override
    public Option<ModuleDescriptor<?>> getPluginModule(String completeModuleKey) {
        ModuleDescriptor module = this.pluginAccessor.getPluginModule(completeModuleKey);
        if (module == null) {
            return Option.none();
        }
        return Option.some(module);
    }

    @Override
    public boolean isPluginModuleEnabled(String completeModuleKey) {
        return this.pluginAccessor.isPluginModuleEnabled(completeModuleKey);
    }
}

