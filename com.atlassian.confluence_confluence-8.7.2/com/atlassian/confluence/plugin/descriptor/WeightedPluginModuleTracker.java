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
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.plugin.web.descriptors.WeightedDescriptor
 *  com.atlassian.plugin.web.descriptors.WeightedDescriptorComparator
 *  com.atlassian.util.concurrent.atomic.AtomicReference
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.plugin.web.descriptors.WeightedDescriptor;
import com.atlassian.plugin.web.descriptors.WeightedDescriptorComparator;
import com.atlassian.util.concurrent.atomic.AtomicReference;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WeightedPluginModuleTracker<M, D extends ModuleDescriptor<M> & WeightedDescriptor>
implements PluginModuleTracker<M, D> {
    private final PluginEventManager pluginEventManager;
    private final ModuleTransformer<D, M> moduleTransformer = new ModuleTransformer();
    private final Class<D> moduleDescriptorClass;
    private final AtomicReference<List<D>> moduleDescriptorsRef = new AtomicReference(new ArrayList());

    public static <M, D extends ModuleDescriptor<M> & WeightedDescriptor> WeightedPluginModuleTracker<M, D> create(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, Class<D> moduleDescriptorClass) {
        return new WeightedPluginModuleTracker<M, D>(pluginAccessor, pluginEventManager, moduleDescriptorClass);
    }

    public WeightedPluginModuleTracker(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, Class<D> moduleDescriptorClass) {
        this.pluginEventManager = pluginEventManager;
        this.moduleDescriptorClass = moduleDescriptorClass;
        pluginEventManager.register((Object)this);
        this.addDescriptors(pluginAccessor.getEnabledModuleDescriptorsByClass(moduleDescriptorClass));
    }

    private void addDescriptors(Iterable<? extends ModuleDescriptor<?>> descriptors) {
        this.moduleDescriptorsRef.update(oldModuleDescriptors -> {
            ArrayList<ModuleDescriptor> moduleDescriptors = new ArrayList<ModuleDescriptor>((Collection<ModuleDescriptor>)oldModuleDescriptors);
            boolean haveSome = false;
            for (ModuleDescriptor descriptor : this.filtered(descriptors)) {
                haveSome = true;
                moduleDescriptors.add(descriptor);
            }
            if (haveSome) {
                Collections.sort(moduleDescriptors, new WeightedDescriptorComparator());
                return moduleDescriptors;
            }
            return oldModuleDescriptors;
        });
    }

    private void removeDescriptors(Iterable<? extends ModuleDescriptor<?>> descriptors) {
        this.moduleDescriptorsRef.update(oldModuleDescriptors -> {
            ArrayList moduleDescriptors = new ArrayList(oldModuleDescriptors);
            boolean haveSome = false;
            for (ModuleDescriptor descriptor : this.filtered(descriptors)) {
                haveSome = true;
                moduleDescriptors.remove(descriptor);
            }
            if (haveSome) {
                Collections.sort(moduleDescriptors, new WeightedDescriptorComparator());
                return moduleDescriptors;
            }
            return oldModuleDescriptors;
        });
    }

    public Iterable<D> getModuleDescriptors() {
        return Iterables.unmodifiableIterable((Iterable)((Iterable)this.moduleDescriptorsRef.get()));
    }

    public Iterable<M> getModules() {
        return Iterables.transform(this.getModuleDescriptors(), this.moduleTransformer);
    }

    public int size() {
        return ((List)this.moduleDescriptorsRef.get()).size();
    }

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

    private Iterable<D> filtered(Iterable<? extends ModuleDescriptor<?>> descriptors) {
        return Iterables.filter(descriptors, this.moduleDescriptorClass);
    }

    private static class ModuleTransformer<D extends ModuleDescriptor<M>, M>
    implements Function<D, M> {
        private ModuleTransformer() {
        }

        public M apply(@NonNull D input) {
            return (M)input.getModule();
        }
    }
}

