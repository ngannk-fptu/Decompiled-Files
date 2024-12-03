/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginRestartState
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginRestartState;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

abstract class ForwardingPluginAccessor
implements PluginAccessor {
    protected final PluginAccessor delegate;

    ForwardingPluginAccessor(PluginAccessor delegate) {
        this.delegate = (PluginAccessor)Preconditions.checkNotNull((Object)delegate);
    }

    public ClassLoader getClassLoader() {
        return this.delegate.getClassLoader();
    }

    public InputStream getDynamicResourceAsStream(String resourcePath) {
        return this.delegate.getDynamicResourceAsStream(resourcePath);
    }

    public <D extends ModuleDescriptor<?>> List<D> getEnabledModuleDescriptorsByClass(Class<D> descriptorClazz) {
        return this.delegate.getEnabledModuleDescriptorsByClass(descriptorClazz);
    }

    @Deprecated
    public <D extends ModuleDescriptor<?>> List<D> getActiveModuleDescriptorsByClass(Class<D> descriptorClazz) {
        return this.delegate.getActiveModuleDescriptorsByClass(descriptorClazz);
    }

    public <M> List<M> getEnabledModulesByClass(Class<M> moduleClass) {
        return this.delegate.getEnabledModulesByClass(moduleClass);
    }

    public Plugin getEnabledPlugin(String pluginKey) {
        return this.delegate.getEnabledPlugin(pluginKey);
    }

    public ModuleDescriptor<?> getEnabledPluginModule(String completeKey) {
        return this.delegate.getEnabledPluginModule(completeKey);
    }

    public Collection<Plugin> getEnabledPlugins() {
        return this.delegate.getEnabledPlugins();
    }

    public <M> Collection<ModuleDescriptor<M>> getModuleDescriptors(Predicate<ModuleDescriptor<M>> moduleDescriptorPredicate) {
        return this.delegate.getModuleDescriptors(moduleDescriptorPredicate);
    }

    public <M> Collection<M> getModules(Predicate<ModuleDescriptor<M>> moduleDescriptorPredicate) {
        return this.delegate.getModules(moduleDescriptorPredicate);
    }

    public Plugin getPlugin(String key) {
        return this.delegate.getPlugin(key);
    }

    public ModuleDescriptor<?> getPluginModule(String completeKey) {
        return this.delegate.getPluginModule(completeKey);
    }

    public PluginRestartState getPluginRestartState(String key) {
        return this.delegate.getPluginRestartState(key);
    }

    public Collection<Plugin> getPlugins() {
        return this.delegate.getPlugins();
    }

    public Collection<Plugin> getPlugins(Predicate<Plugin> pluginPredicate) {
        return this.delegate.getPlugins(pluginPredicate);
    }

    public boolean isPluginEnabled(String key) {
        return this.delegate.isPluginEnabled(key);
    }

    public boolean isPluginModuleEnabled(String completeKey) {
        return this.delegate.isPluginModuleEnabled(completeKey);
    }

    public boolean isSystemPlugin(String key) {
        return this.delegate.isSystemPlugin(key);
    }

    public Iterable<ModuleDescriptor<?>> getDynamicModules(Plugin plugin) {
        return this.delegate.getDynamicModules(plugin);
    }
}

