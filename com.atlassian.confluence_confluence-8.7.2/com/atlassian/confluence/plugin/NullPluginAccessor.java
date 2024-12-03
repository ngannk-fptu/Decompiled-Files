/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 *  com.atlassian.plugin.predicate.PluginPredicate
 */
package com.atlassian.confluence.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import com.atlassian.plugin.predicate.PluginPredicate;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class NullPluginAccessor
implements PluginAccessor {
    public Collection<Plugin> getPlugins() {
        return Collections.emptyList();
    }

    @Deprecated
    public Collection<Plugin> getPlugins(PluginPredicate pluginPredicate) {
        return Collections.emptyList();
    }

    public Collection<Plugin> getPlugins(Predicate<Plugin> pluginPredicate) {
        return Collections.emptyList();
    }

    public Collection<Plugin> getEnabledPlugins() {
        return Collections.emptyList();
    }

    @Deprecated
    public <M> Collection<M> getModules(ModuleDescriptorPredicate<M> moduleDescriptorPredicate) {
        return Collections.emptyList();
    }

    @Deprecated
    public <M> Collection<ModuleDescriptor<M>> getModuleDescriptors(ModuleDescriptorPredicate<M> moduleDescriptorPredicate) {
        return Collections.emptyList();
    }

    public <M> Collection<M> getModules(Predicate<ModuleDescriptor<M>> moduleDescriptorPredicate) {
        return Collections.emptyList();
    }

    public <M> Collection<ModuleDescriptor<M>> getModuleDescriptors(Predicate<ModuleDescriptor<M>> moduleDescriptorPredicate) {
        return Collections.emptyList();
    }

    public Plugin getPlugin(String key) throws IllegalArgumentException {
        return null;
    }

    public Plugin getEnabledPlugin(String pluginKey) throws IllegalArgumentException {
        return null;
    }

    public ModuleDescriptor<?> getPluginModule(String completeKey) {
        return null;
    }

    public ModuleDescriptor<?> getEnabledPluginModule(String completeKey) {
        return null;
    }

    public boolean isPluginEnabled(String key) throws IllegalArgumentException {
        return false;
    }

    public boolean isPluginModuleEnabled(String completeKey) {
        return false;
    }

    public <M> List<M> getEnabledModulesByClass(Class<M> moduleClass) {
        return Collections.emptyList();
    }

    public <D extends ModuleDescriptor<?>> List<D> getEnabledModuleDescriptorsByClass(Class<D> descriptorClazz) {
        return Collections.emptyList();
    }

    public InputStream getDynamicResourceAsStream(String resourcePath) {
        return null;
    }

    public ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    public boolean isSystemPlugin(String key) {
        return false;
    }

    public PluginRestartState getPluginRestartState(String key) {
        return PluginRestartState.NONE;
    }

    public Iterable<ModuleDescriptor<?>> getDynamicModules(Plugin plugin) {
        return Collections.emptyList();
    }
}

