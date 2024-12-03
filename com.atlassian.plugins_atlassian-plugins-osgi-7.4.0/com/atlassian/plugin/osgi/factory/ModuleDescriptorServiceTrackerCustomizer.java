/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleAvailableEvent
 *  com.atlassian.plugin.event.events.PluginModuleUnavailableEvent
 *  com.google.common.base.Preconditions
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleAvailableEvent;
import com.atlassian.plugin.event.events.PluginModuleUnavailableEvent;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.google.common.base.Preconditions;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ModuleDescriptorServiceTrackerCustomizer
implements ServiceTrackerCustomizer {
    private static final Logger log = LoggerFactory.getLogger(ModuleDescriptorServiceTrackerCustomizer.class);
    private final Bundle bundle;
    private final OsgiPlugin plugin;
    private final PluginEventManager pluginEventManager;

    public ModuleDescriptorServiceTrackerCustomizer(OsgiPlugin plugin, PluginEventManager pluginEventManager) {
        this.plugin = (OsgiPlugin)Preconditions.checkNotNull((Object)plugin);
        this.bundle = (Bundle)Preconditions.checkNotNull((Object)plugin.getBundle());
        this.pluginEventManager = (PluginEventManager)Preconditions.checkNotNull((Object)pluginEventManager);
    }

    public Object addingService(ServiceReference serviceReference) {
        ModuleDescriptor descriptor = null;
        if (serviceReference.getBundle() == this.bundle) {
            descriptor = (ModuleDescriptor)this.bundle.getBundleContext().getService(serviceReference);
            this.plugin.addModuleDescriptor(descriptor);
            if (log.isInfoEnabled()) {
                log.info("Dynamically registered new module descriptor: {}", (Object)descriptor.getCompleteKey());
            }
            this.pluginEventManager.broadcast((Object)new PluginModuleAvailableEvent(descriptor));
        }
        return descriptor;
    }

    public void modifiedService(ServiceReference serviceReference, Object o) {
    }

    public void removedService(ServiceReference serviceReference, Object o) {
        if (serviceReference.getBundle() == this.bundle) {
            ModuleDescriptor descriptor = (ModuleDescriptor)o;
            this.pluginEventManager.broadcast((Object)new PluginModuleUnavailableEvent(descriptor));
            this.plugin.clearModuleDescriptor(descriptor.getKey());
            if (log.isInfoEnabled()) {
                log.info("Dynamically removed module descriptor: {}", (Object)descriptor.getCompleteKey());
            }
        }
    }
}

