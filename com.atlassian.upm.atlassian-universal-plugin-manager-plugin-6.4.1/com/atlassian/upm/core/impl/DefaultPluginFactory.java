/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 */
package com.atlassian.upm.core.impl;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginFactory;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.impl.PluginImpl;
import com.atlassian.upm.core.impl.PluginModuleImpl;
import com.atlassian.upm.spi.PluginControlHandler;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Objects;
import java.util.Set;

public class DefaultPluginFactory
implements PluginFactory {
    private static final String UPM_PLUGIN_KEY = "com.atlassian.upm.atlassian-universal-plugin-manager-plugin";
    private final I18nResolver i18nResolver;
    private final PluginAccessor accessor;
    private final PluginMetadataAccessor metadata;
    protected final PluginControlHandlerRegistry pluginControlHandlerRegistry;

    public DefaultPluginFactory(I18nResolver i18nResolver, PluginAccessor accessor, PluginMetadataAccessor metadata, PluginControlHandlerRegistry pluginControlHandlerRegistry) {
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.accessor = Objects.requireNonNull(accessor, "accessor");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.pluginControlHandlerRegistry = Objects.requireNonNull(pluginControlHandlerRegistry, "pluginControlHandlerRegistry");
    }

    @Override
    public Plugin createPlugin(com.atlassian.plugin.Plugin plugin) {
        return new PluginImpl(plugin, this.getPluginAttributes(plugin, Option.none(Boolean.class)), this, this.metadata);
    }

    private Plugin createPlugin(com.atlassian.plugin.Plugin plugin, Option<Boolean> updateAvailable) {
        return new PluginImpl(plugin, this.getPluginAttributes(plugin, updateAvailable), this, this.metadata);
    }

    @Override
    public Iterable<Plugin> createPlugins(Iterable<com.atlassian.plugin.Plugin> plugins) {
        return ImmutableList.copyOf((Iterable)Iterables.transform(plugins, (Function)new Function<com.atlassian.plugin.Plugin, Plugin>(){

            public Plugin apply(com.atlassian.plugin.Plugin plugin) {
                return DefaultPluginFactory.this.createPlugin(plugin);
            }
        }));
    }

    @Override
    public Iterable<Plugin> createPlugins(Iterable<com.atlassian.plugin.Plugin> plugins, Iterable<Addon> availablePluginUpdates) {
        ImmutableSet updatablePluginKeys = ImmutableSet.copyOf((Iterable)Iterables.transform(availablePluginUpdates, (Function)new Function<Addon, String>(){

            public String apply(Addon mpacPlugin) {
                return mpacPlugin.getKey();
            }
        }));
        return ImmutableList.copyOf((Iterable)Iterables.transform(plugins, (Function)new Function<com.atlassian.plugin.Plugin, Plugin>((Set)updatablePluginKeys){
            final /* synthetic */ Set val$updatablePluginKeys;
            {
                this.val$updatablePluginKeys = set;
            }

            public Plugin apply(com.atlassian.plugin.Plugin plugin) {
                return DefaultPluginFactory.this.createPlugin(plugin, Option.some(Iterables.contains((Iterable)this.val$updatablePluginKeys, (Object)plugin.getKey())));
            }
        }));
    }

    @Override
    public Plugin.Module createModule(ModuleDescriptor<?> module) {
        return this.createModule(module, this.createPlugin(module.getPlugin()));
    }

    @Override
    public Plugin.Module createModule(ModuleDescriptor<?> module, Plugin plugin) {
        return new PluginModuleImpl(module, this.i18nResolver, plugin);
    }

    private PluginImpl.PluginAttributes getPluginAttributes(com.atlassian.plugin.Plugin plugin, Option<Boolean> updateAvailable) {
        boolean isEnabled = false;
        boolean handled = false;
        PluginRestartState restartState = PluginRestartState.NONE;
        boolean isConnect = Plugins.isConnectPlugin(plugin, this.pluginControlHandlerRegistry);
        for (PluginControlHandler handler : this.getControlHandlers()) {
            if (!handler.canControl(plugin.getKey())) continue;
            isEnabled = handler.isPluginEnabled(plugin.getKey());
            restartState = handler.getPluginRestartState(plugin.getKey());
            handled = true;
            break;
        }
        if (!handled) {
            isEnabled = this.accessor.isPluginEnabled(plugin.getKey());
            restartState = this.accessor.getPluginRestartState(plugin.getKey());
        }
        return new PluginImpl.PluginAttributes(isConnect, isEnabled, this.getPluginName(plugin), restartState, this.isUninstallPreventedByAdditionalCriteria(plugin, isConnect), updateAvailable, UPM_PLUGIN_KEY.equals(plugin.getKey()));
    }

    protected boolean isUninstallPreventedByAdditionalCriteria(com.atlassian.plugin.Plugin plugin, boolean isConnect) {
        return false;
    }

    private String getPluginName(com.atlassian.plugin.Plugin plugin) {
        String i18nNameKey = plugin.getI18nNameKey();
        if (i18nNameKey != null && this.i18nResolver.getText(i18nNameKey) != null && !this.i18nResolver.getText(i18nNameKey).equals(i18nNameKey)) {
            return this.i18nResolver.getText(i18nNameKey);
        }
        return plugin.getName();
    }

    private Iterable<PluginControlHandler> getControlHandlers() {
        return this.pluginControlHandlerRegistry.getHandlers();
    }
}

