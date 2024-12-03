/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginRegistry$ReadOnly
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.scope.ScopeManager
 *  com.atlassian.plugin.util.Assertions
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginInternal;
import com.atlassian.plugin.PluginRegistry;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.classloader.PluginsClassLoader;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import com.atlassian.plugin.predicate.EnabledModulePredicate;
import com.atlassian.plugin.predicate.ModuleOfClassPredicate;
import com.atlassian.plugin.scope.ScopeManager;
import com.atlassian.plugin.util.Assertions;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductPluginAccessorBase
implements PluginAccessor {
    private static final Logger log = LoggerFactory.getLogger(ProductPluginAccessorBase.class);
    private final PluginRegistry.ReadOnly pluginRegistry;
    private final ModuleDescriptorFactory moduleDescriptorFactory;
    private final ClassLoader classLoader;
    private final PluginPersistentStateStore store;

    public ProductPluginAccessorBase(PluginRegistry.ReadOnly pluginRegistry, PluginPersistentStateStore store, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager) {
        this.pluginRegistry = pluginRegistry;
        this.moduleDescriptorFactory = (ModuleDescriptorFactory)Assertions.notNull((String)"ModuleDescriptorFactory", (Object)moduleDescriptorFactory);
        this.classLoader = new PluginsClassLoader(null, this, pluginEventManager);
        this.store = (PluginPersistentStateStore)Assertions.notNull((String)"PluginPersistentStateStore", (Object)store);
    }

    @Deprecated
    public ProductPluginAccessorBase(PluginRegistry.ReadOnly pluginRegistry, PluginPersistentStateStore store, ModuleDescriptorFactory moduleDescriptorFactory, PluginEventManager pluginEventManager, ScopeManager ignored) {
        this(pluginRegistry, store, moduleDescriptorFactory, pluginEventManager);
    }

    public Collection<Plugin> getPlugins() {
        return this.pluginRegistry.getAll();
    }

    public Collection<Plugin> getPlugins(Predicate<Plugin> pluginPredicate) {
        Assertions.notNull((String)"pluginPredicate", pluginPredicate);
        return this.getPlugins().stream().filter(pluginPredicate).collect(Collectors.toList());
    }

    public Collection<Plugin> getEnabledPlugins() {
        return this.getPlugins(p -> PluginState.ENABLED.equals((Object)p.getPluginState()));
    }

    private <M> Stream<M> getModules(Stream<ModuleDescriptor<M>> moduleDescriptors) {
        return moduleDescriptors.filter(Objects::nonNull).map(md -> {
            try {
                return md.getModule();
            }
            catch (RuntimeException ex) {
                log.error("Exception when retrieving plugin module {}", (Object)md.getCompleteKey(), (Object)ex);
                md.setBroken();
                return null;
            }
        }).filter(Objects::nonNull);
    }

    private <M> Stream<ModuleDescriptor<M>> getModuleDescriptors(Collection<Plugin> plugins, Predicate<ModuleDescriptor<M>> predicate) {
        return plugins.stream().flatMap(p -> p.getModuleDescriptors().stream()).map(m -> {
            ModuleDescriptor cast = m;
            return cast;
        }).filter(predicate);
    }

    public <M> Collection<M> getModules(Predicate<ModuleDescriptor<M>> predicate) {
        Assertions.notNull((String)"moduleDescriptorPredicate", predicate);
        return this.getModules(this.getModuleDescriptors(this.getPlugins(), predicate)).collect(Collectors.toList());
    }

    public <M> Collection<ModuleDescriptor<M>> getModuleDescriptors(Predicate<ModuleDescriptor<M>> predicate) {
        Assertions.notNull((String)"moduleDescriptorPredicate", predicate);
        return this.getModuleDescriptors(this.getPlugins(), predicate).collect(Collectors.toList());
    }

    public Plugin getPlugin(String key) {
        return this.pluginRegistry.get((String)Assertions.notNull((String)"Plugin key ", (Object)key));
    }

    public Plugin getEnabledPlugin(String pluginKey) {
        if (!this.isPluginEnabled(pluginKey)) {
            return null;
        }
        return this.getPlugin(pluginKey);
    }

    private ModuleDescriptor<?> getPluginModule(ModuleCompleteKey key) {
        Plugin plugin = this.getPlugin(key.getPluginKey());
        if (plugin == null) {
            return null;
        }
        return plugin.getModuleDescriptor(key.getModuleKey());
    }

    public ModuleDescriptor<?> getPluginModule(@Nullable String completeKey) {
        return this.getPluginModule(new ModuleCompleteKey(completeKey));
    }

    private boolean isPluginModuleEnabled(ModuleCompleteKey key) {
        if (!this.isPluginEnabled(key.getPluginKey())) {
            return false;
        }
        ModuleDescriptor<?> pluginModule = this.getPluginModule(key);
        return pluginModule != null && pluginModule.isEnabled();
    }

    public ModuleDescriptor<?> getEnabledPluginModule(@Nullable String completeKey) {
        ModuleCompleteKey key = new ModuleCompleteKey(completeKey);
        if (!this.isPluginModuleEnabled(key)) {
            return null;
        }
        return this.getEnabledPlugin(key.getPluginKey()).getModuleDescriptor(key.getModuleKey());
    }

    public boolean isPluginEnabled(String key) {
        Plugin plugin = this.pluginRegistry.get((String)Assertions.notNull((String)"Plugin key", (Object)key));
        return plugin != null && plugin.getPluginState() == PluginState.ENABLED;
    }

    public boolean isPluginModuleEnabled(@Nullable String completeKey) {
        return completeKey != null && this.isPluginModuleEnabled(new ModuleCompleteKey(completeKey));
    }

    private <M> Stream<ModuleDescriptor<M>> getEnabledModuleDescriptorsByModuleClass(Class<M> moduleClass) {
        ModuleOfClassPredicate<M> ofType = new ModuleOfClassPredicate<M>(moduleClass);
        EnabledModulePredicate enabled = new EnabledModulePredicate();
        return this.getModuleDescriptors(this.getEnabledPlugins(), ofType.and(enabled));
    }

    public <M> List<M> getEnabledModulesByClass(Class<M> moduleClass) {
        return this.getModules(this.getEnabledModuleDescriptorsByModuleClass(moduleClass)).collect(Collectors.toList());
    }

    public <D extends ModuleDescriptor<?>> List<D> getEnabledModuleDescriptorsByClass(Class<D> descriptorClazz) {
        Assertions.notNull((String)"Descriptor class", descriptorClazz);
        return this.getEnabledPlugins().stream().flatMap(p -> p.getModuleDescriptors().stream()).filter(descriptorClazz::isInstance).filter(new EnabledModulePredicate()).map(descriptorClazz::cast).collect(Collectors.toList());
    }

    public InputStream getDynamicResourceAsStream(String resourcePath) {
        return this.getClassLoader().getResourceAsStream(resourcePath);
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public boolean isSystemPlugin(String key) {
        Plugin plugin = this.getPlugin(key);
        return plugin != null && plugin.isSystemPlugin();
    }

    public PluginRestartState getPluginRestartState(String key) {
        return this.store.load().getPluginRestartState(key);
    }

    public Iterable<ModuleDescriptor<?>> getDynamicModules(Plugin plugin) {
        if (plugin instanceof PluginInternal) {
            return ((PluginInternal)plugin).getDynamicModuleDescriptors();
        }
        throw new IllegalArgumentException(plugin + " does not implement com.atlassian.plugin.PluginInternal it is a " + plugin.getClass().getCanonicalName());
    }
}

