/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.macro.metadata;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.macro.metadata.AllMacroMetadataProvider;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.MacroMetadataSource;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AllMacroMetadataCache<T extends ModuleDescriptor<Macro> & MacroMetadataSource>
implements AllMacroMetadataProvider<T> {
    private final Cache<Class<? extends MacroMetadataSource>, Map<String, MacroMetadata>> cache;
    private final EventListenerRegistrar eventListenerRegistrar;
    private final AllMacroMetadataProvider<T> allMacroMetadataProvider;

    public AllMacroMetadataCache(CacheFactory cacheFactory, AllMacroMetadataProvider<T> allMacroMetadataProvider, EventListenerRegistrar eventListenerRegistrar) {
        this.allMacroMetadataProvider = Objects.requireNonNull(allMacroMetadataProvider);
        this.cache = CoreCache.MACRO_METADATA.getCache(cacheFactory);
        this.eventListenerRegistrar = Objects.requireNonNull(eventListenerRegistrar);
    }

    @PostConstruct
    public void registerForEvents() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void unregisterForEvents() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @Override
    public @NonNull Map<String, MacroMetadata> apply(Class<T> descriptorClass, ModuleDescriptorPredicate<Macro> descriptorPredicate) {
        return (Map)this.cache.get(descriptorClass, () -> (Map)this.allMacroMetadataProvider.apply(descriptorClass, descriptorPredicate));
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.flushCacheForPluginModule(event.getModule());
    }

    @PluginEventListener
    public void onPluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.flushCacheForPluginModule(event.getModule());
    }

    private void flushCacheForPluginModule(ModuleDescriptor<?> moduleDescriptor) {
        this.cache.getKeys().stream().filter(moduleDescriptorClass -> moduleDescriptorClass.isInstance(moduleDescriptor)).findAny().ifPresent(arg_0 -> this.cache.remove(arg_0));
    }
}

