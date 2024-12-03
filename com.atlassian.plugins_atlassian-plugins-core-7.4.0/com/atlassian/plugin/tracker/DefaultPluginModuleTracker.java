/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 */
package com.atlassian.plugin.tracker;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArraySet;

public class DefaultPluginModuleTracker<M, T extends ModuleDescriptor<M>>
implements PluginModuleTracker<M, T> {
    private final PluginEventManager pluginEventManager;
    private final Class<T> moduleDescriptorClass;
    private final PluginModuleTracker.Customizer<M, T> pluginModuleTrackerCustomizer;
    private final CopyOnWriteArraySet<T> moduleDescriptors = new CopyOnWriteArraySet();
    private final ModuleTransformer<M, T> moduleTransformer = new ModuleTransformer();

    public DefaultPluginModuleTracker(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, Class<T> moduleDescriptorClass) {
        this(pluginAccessor, pluginEventManager, moduleDescriptorClass, new NoOpPluginModuleTrackerCustomizer());
    }

    public DefaultPluginModuleTracker(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, Class<T> moduleDescriptorClass, PluginModuleTracker.Customizer<M, T> pluginModuleTrackerCustomizer) {
        this.pluginEventManager = pluginEventManager;
        this.moduleDescriptorClass = moduleDescriptorClass;
        this.pluginModuleTrackerCustomizer = pluginModuleTrackerCustomizer;
        pluginEventManager.register((Object)this);
        this.addDescriptors(pluginAccessor.getEnabledModuleDescriptorsByClass(moduleDescriptorClass));
    }

    @Override
    public Iterable<T> getModuleDescriptors() {
        return Iterables.unmodifiableIterable(this.moduleDescriptors);
    }

    @Override
    public Iterable<M> getModules() {
        return Iterables.transform(this.getModuleDescriptors(), this.moduleTransformer);
    }

    @Override
    public int size() {
        return this.moduleDescriptors.size();
    }

    @Override
    public void close() {
        this.pluginEventManager.unregister((Object)this);
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.addDescriptors(Collections.singleton(event.getModule()));
    }

    @PluginEventListener
    public void onPluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.removeDescriptors(Collections.singleton(event.getModule()));
    }

    @PluginEventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        this.removeDescriptors(event.getPlugin().getModuleDescriptors());
    }

    private void addDescriptors(Iterable<? extends ModuleDescriptor<?>> descriptors) {
        for (ModuleDescriptor descriptor : this.filtered(descriptors)) {
            ModuleDescriptor customized = this.pluginModuleTrackerCustomizer.adding(descriptor);
            if (customized == null) continue;
            this.moduleDescriptors.add(customized);
        }
    }

    private void removeDescriptors(Iterable<? extends ModuleDescriptor<?>> descriptors) {
        for (ModuleDescriptor descriptor : this.filtered(descriptors)) {
            if (!this.moduleDescriptors.remove(descriptor)) continue;
            this.pluginModuleTrackerCustomizer.removed(descriptor);
        }
    }

    private Iterable<T> filtered(Iterable<? extends ModuleDescriptor<?>> descriptors) {
        return Iterables.filter(descriptors, this.moduleDescriptorClass);
    }

    public static <M, T extends ModuleDescriptor<M>> PluginModuleTracker<M, T> create(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, Class<? extends ModuleDescriptor<?>> moduleDescriptorClass) {
        Class<? extends ModuleDescriptor<?>> klass = moduleDescriptorClass;
        return new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, klass);
    }

    private static class ModuleTransformer<M, T extends ModuleDescriptor<M>>
    implements Function<T, M> {
        private ModuleTransformer() {
        }

        public M apply(T from) {
            return (M)from.getModule();
        }
    }

    private static class NoOpPluginModuleTrackerCustomizer<M, T extends ModuleDescriptor<M>>
    implements PluginModuleTracker.Customizer<M, T> {
        private NoOpPluginModuleTrackerCustomizer() {
        }

        @Override
        public T adding(T descriptor) {
            return descriptor;
        }

        @Override
        public void removed(T descriptor) {
        }
    }
}

