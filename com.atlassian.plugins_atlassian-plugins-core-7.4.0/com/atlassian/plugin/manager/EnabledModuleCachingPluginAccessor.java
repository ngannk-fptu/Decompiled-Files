/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.instrumentation.PluginSystemInstrumentation;
import com.atlassian.plugin.instrumentation.Timer;
import com.atlassian.plugin.manager.ForwardingPluginAccessor;
import com.atlassian.plugin.manager.SafeModuleExtractor;
import com.atlassian.plugin.predicate.EnabledModulePredicate;
import com.atlassian.plugin.predicate.ModuleOfClassPredicate;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public final class EnabledModuleCachingPluginAccessor
extends ForwardingPluginAccessor
implements PluginAccessor {
    private static final long DESCRIPTOR_TIMEOUT_SEC = Long.getLong("com.atlassian.plugin.descriptor.class.cache.timeout.sec", 1800L);
    private static final long MODULE_TIMEOUT_SEC = Long.getLong("com.atlassian.plugin.module.class.cache.timeout.sec", 1800L);
    private final SafeModuleExtractor safeModuleExtractor;
    private final LoadingCache<Class<ModuleDescriptor<Object>>, List<ModuleDescriptor<Object>>> cacheByDescriptorClass = CacheBuilder.newBuilder().expireAfterAccess(DESCRIPTOR_TIMEOUT_SEC, TimeUnit.SECONDS).build((CacheLoader)new ByModuleDescriptorClassCacheLoader());
    private final LoadingCache<Class<?>, List<ModuleDescriptor>> cacheByModuleClass = CacheBuilder.newBuilder().expireAfterAccess(MODULE_TIMEOUT_SEC, TimeUnit.SECONDS).build((CacheLoader)new ByModuleClassCacheLoader());

    public EnabledModuleCachingPluginAccessor(PluginAccessor delegate, PluginEventManager pluginEventManager, PluginController pluginController) {
        super(delegate);
        Preconditions.checkNotNull((Object)pluginEventManager);
        this.safeModuleExtractor = new SafeModuleExtractor(pluginController);
        pluginEventManager.register((Object)this);
    }

    @PluginEventListener
    public void onPluginDisable(PluginDisabledEvent event) {
        this.invalidateAll();
    }

    @PluginEventListener
    public void onPluginEnable(PluginEnabledEvent event) {
        this.invalidateAll();
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.invalidateAll();
    }

    @PluginEventListener
    public void onPluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.invalidateAll();
    }

    @PluginEventListener
    public void onPluginFrameworkShutdown(PluginFrameworkShutdownEvent event) {
        this.invalidateAll();
    }

    private void invalidateAll() {
        this.cacheByDescriptorClass.invalidateAll();
        this.cacheByModuleClass.invalidateAll();
    }

    @Override
    public <D extends ModuleDescriptor<?>> List<D> getEnabledModuleDescriptorsByClass(Class<D> descriptorClazz) {
        try (Timer ignored = PluginSystemInstrumentation.instance().pullTimer("getEnabledModuleDescriptorsByClass");){
            List descriptors;
            List list = descriptors = (List)this.cacheByDescriptorClass.getUnchecked(descriptorClazz);
            return list;
        }
    }

    @Override
    public <M> List<M> getEnabledModulesByClass(Class<M> moduleClass) {
        try (Timer ignored = PluginSystemInstrumentation.instance().pullTimer("getEnabledModulesByClass");){
            List descriptors = (List)this.cacheByModuleClass.getUnchecked(moduleClass);
            List list = this.safeModuleExtractor.getModules(descriptors);
            return list;
        }
    }

    private <M> List<ModuleDescriptor<M>> getEnabledModuleDescriptorsByModuleClass(Class<M> moduleClass) {
        ModuleOfClassPredicate<M> ofType = new ModuleOfClassPredicate<M>(moduleClass);
        EnabledModulePredicate enabled = new EnabledModulePredicate();
        return this.getModuleDescriptors(this.delegate.getEnabledPlugins(), ofType.and(enabled));
    }

    private <M> List<ModuleDescriptor<M>> getModuleDescriptors(Collection<Plugin> plugins, Predicate<ModuleDescriptor<M>> predicate) {
        return plugins.stream().flatMap(plugin -> plugin.getModuleDescriptors().stream()).map(moduleDescriptor -> moduleDescriptor).filter(predicate).collect(Collectors.toList());
    }

    private class ByModuleClassCacheLoader
    extends CacheLoader<Class, List<ModuleDescriptor>> {
        private ByModuleClassCacheLoader() {
        }

        public List<ModuleDescriptor> load(@Nonnull Class moduleClass) {
            return EnabledModuleCachingPluginAccessor.this.getEnabledModuleDescriptorsByModuleClass(moduleClass);
        }
    }

    private class ByModuleDescriptorClassCacheLoader
    extends CacheLoader<Class<ModuleDescriptor<Object>>, List<ModuleDescriptor<Object>>> {
        private ByModuleDescriptorClassCacheLoader() {
        }

        public List<ModuleDescriptor<Object>> load(@Nonnull Class<ModuleDescriptor<Object>> moduleDescriptorClass) {
            return EnabledModuleCachingPluginAccessor.this.delegate.getEnabledModuleDescriptorsByClass(moduleDescriptorClass);
        }
    }
}

