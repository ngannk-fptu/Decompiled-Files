/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ModuleDescriptorCache<T extends ModuleDescriptor<?>> {
    private static final Logger log = LoggerFactory.getLogger(ModuleDescriptorCache.class);
    private final Map<String, T> descriptors = Maps.newConcurrentMap();
    private final Class<? extends T> moduleDescriptorClass;
    private final ModuleDescriptorPredicate moduleDescriptorPredicate;
    private final Lock initializeLock = new ReentrantLock();

    public ModuleDescriptorCache(Class<? extends T> moduleDescriptorClass) {
        this.moduleDescriptorClass = moduleDescriptorClass;
        this.moduleDescriptorPredicate = null;
    }

    public ModuleDescriptorCache(Class<? extends T> moduleDescriptorClass, ModuleDescriptorPredicate moduleDescriptorPredicate) {
        this.moduleDescriptorClass = moduleDescriptorClass;
        this.moduleDescriptorPredicate = moduleDescriptorPredicate;
    }

    @PluginEventListener
    public void pluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.initializeLock.lock();
        try {
            ModuleDescriptor moduleDescriptor = event.getModule();
            if (this.accept(moduleDescriptor)) {
                log.info("Adding active plugin module to cache: " + moduleDescriptor.getCompleteKey());
                this.descriptors.put(moduleDescriptor.getCompleteKey(), moduleDescriptor);
            } else if (this.descriptors.remove(moduleDescriptor.getCompleteKey()) != null && log.isDebugEnabled()) {
                log.debug("the {} on the cache for {} decided to add {} on initialize, but decided against it on pluginModuleEnabled", new Object[]{this.moduleDescriptorPredicate.getClass().getName(), this.moduleDescriptorClass.getName(), moduleDescriptor.getCompleteKey()});
            }
        }
        finally {
            this.initializeLock.unlock();
        }
    }

    boolean accept(ModuleDescriptor moduleDescriptor) {
        return this.moduleDescriptorClass.isInstance(moduleDescriptor) && (this.moduleDescriptorPredicate == null || this.moduleDescriptorPredicate.matches(moduleDescriptor));
    }

    @PluginEventListener
    public synchronized void pluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.initializeLock.lock();
        try {
            ModuleDescriptor moduleDescriptor = event.getModule();
            if (this.moduleDescriptorClass.isInstance(moduleDescriptor)) {
                log.info("Removing inactive plugin module from cache: " + moduleDescriptor.getCompleteKey());
                this.descriptors.remove(moduleDescriptor.getCompleteKey());
            }
        }
        finally {
            this.initializeLock.unlock();
        }
    }

    public Collection<T> getDescriptors() {
        return this.descriptors.values();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void initialize(Initializer<T> initializer) {
        this.initializeLock.lock();
        try {
            this.descriptors.clear();
            for (ModuleDescriptor descriptor : initializer.getDescriptors()) {
                this.descriptors.put(descriptor.getCompleteKey(), descriptor);
            }
        }
        finally {
            this.initializeLock.unlock();
        }
    }

    public static interface Initializer<T> {
        public Collection<? extends T> getDescriptors();
    }
}

