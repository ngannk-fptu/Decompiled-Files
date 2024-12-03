/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import com.atlassian.plugin.predicate.PluginPredicate;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface PluginAccessor {
    public Collection<Plugin> getPlugins();

    @Deprecated
    default public Collection<Plugin> getPlugins(PluginPredicate pluginPredicate) {
        return this.getPlugins(pluginPredicate::matches);
    }

    public Collection<Plugin> getPlugins(Predicate<Plugin> var1);

    public Collection<Plugin> getEnabledPlugins();

    @Deprecated
    default public <M> Collection<M> getModules(ModuleDescriptorPredicate<M> moduleDescriptorPredicate) {
        return this.getModules(moduleDescriptorPredicate::matches);
    }

    @Deprecated
    default public <M> Collection<ModuleDescriptor<M>> getModuleDescriptors(ModuleDescriptorPredicate<M> moduleDescriptorPredicate) {
        return this.getModuleDescriptors(moduleDescriptorPredicate::matches);
    }

    public <M> Collection<M> getModules(Predicate<ModuleDescriptor<M>> var1);

    public <M> Collection<ModuleDescriptor<M>> getModuleDescriptors(Predicate<ModuleDescriptor<M>> var1);

    public Plugin getPlugin(String var1);

    public Plugin getEnabledPlugin(String var1);

    public ModuleDescriptor<?> getPluginModule(String var1);

    public ModuleDescriptor<?> getEnabledPluginModule(String var1);

    public boolean isPluginEnabled(String var1);

    public boolean isPluginModuleEnabled(String var1);

    public <M> List<M> getEnabledModulesByClass(Class<M> var1);

    public <D extends ModuleDescriptor<?>> List<D> getEnabledModuleDescriptorsByClass(Class<D> var1);

    @Deprecated
    default public <D extends ModuleDescriptor<?>> List<D> getActiveModuleDescriptorsByClass(Class<D> descriptorClazz) {
        return this.getEnabledModuleDescriptorsByClass(descriptorClazz);
    }

    public InputStream getDynamicResourceAsStream(String var1);

    public ClassLoader getClassLoader();

    public boolean isSystemPlugin(String var1);

    public PluginRestartState getPluginRestartState(String var1);

    public Iterable<ModuleDescriptor<?>> getDynamicModules(Plugin var1);

    public static final class Descriptor {
        public static final String FILENAME = "atlassian-plugin.xml";

        private Descriptor() {
        }
    }
}

