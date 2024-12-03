/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleAvailableEvent
 *  com.atlassian.plugin.event.events.PluginModuleUnavailableEvent
 *  com.google.common.base.Preconditions
 *  org.dom4j.Element
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleAvailableEvent;
import com.atlassian.plugin.event.events.PluginModuleUnavailableEvent;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Element;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UnrecognizedModuleDescriptorServiceTrackerCustomizer
implements ServiceTrackerCustomizer {
    private static final Logger log = LoggerFactory.getLogger(UnrecognizedModuleDescriptorServiceTrackerCustomizer.class);
    private final Bundle bundle;
    private final OsgiPlugin plugin;
    private final PluginEventManager pluginEventManager;

    public UnrecognizedModuleDescriptorServiceTrackerCustomizer(OsgiPlugin plugin, PluginEventManager pluginEventManager) {
        this.plugin = (OsgiPlugin)Preconditions.checkNotNull((Object)plugin);
        this.bundle = (Bundle)Preconditions.checkNotNull((Object)plugin.getBundle());
        this.pluginEventManager = (PluginEventManager)Preconditions.checkNotNull((Object)pluginEventManager);
    }

    public Object addingService(ServiceReference serviceReference) {
        ListableModuleDescriptorFactory factory = (ListableModuleDescriptorFactory)this.bundle.getBundleContext().getService(serviceReference);
        if (this.canFactoryResolveUnrecognizedDescriptor(factory) || this.isFactoryInUse(factory)) {
            return factory;
        }
        this.bundle.getBundleContext().ungetService(serviceReference);
        return null;
    }

    private boolean canFactoryResolveUnrecognizedDescriptor(ListableModuleDescriptorFactory factory) {
        boolean usedFactory = false;
        for (UnrecognisedModuleDescriptor unrecognised : this.getModuleDescriptorsByDescriptorClass(UnrecognisedModuleDescriptor.class)) {
            Element source = this.plugin.getModuleElements().get(unrecognised.getKey());
            if (source == null || !factory.hasModuleDescriptor(source.getName())) continue;
            try {
                if (this.isRestDescriptorNotResolvable(factory, source)) {
                    return false;
                }
                ModuleDescriptor descriptor = factory.getModuleDescriptor(source.getName());
                descriptor.init(unrecognised.getPlugin(), source);
                this.plugin.addModuleDescriptor(descriptor);
                log.info("Turned unrecognized plugin module {} into module {}", (Object)descriptor.getCompleteKey(), (Object)descriptor);
                this.pluginEventManager.broadcast((Object)new PluginModuleAvailableEvent(descriptor));
            }
            catch (Exception e) {
                log.error("Unable to transform {} into actual plugin module using factory {}", new Object[]{unrecognised.getCompleteKey(), factory, e});
                unrecognised.setErrorText(e.getMessage());
            }
        }
        return usedFactory;
    }

    private boolean isFactoryInUse(ListableModuleDescriptorFactory factory) {
        for (ModuleDescriptor descriptor : this.plugin.getModuleDescriptors()) {
            for (Class<? extends ModuleDescriptor> descriptorClass : factory.getModuleDescriptorClasses()) {
                if (descriptorClass != descriptor.getClass()) continue;
                return true;
            }
        }
        return false;
    }

    public void modifiedService(ServiceReference serviceReference, Object o) {
    }

    public void removedService(ServiceReference serviceReference, Object o) {
        ListableModuleDescriptorFactory factory = (ListableModuleDescriptorFactory)o;
        for (Class<? extends ModuleDescriptor> moduleDescriptorClass : factory.getModuleDescriptorClasses()) {
            for (ModuleDescriptor moduleDescriptor : this.getModuleDescriptorsByDescriptorClass(moduleDescriptorClass)) {
                if (this.plugin.getPluginState() == PluginState.ENABLED) {
                    this.pluginEventManager.broadcast((Object)new PluginModuleUnavailableEvent(moduleDescriptor));
                    log.info("Removed plugin module {} as its factory was uninstalled", (Object)moduleDescriptor.getCompleteKey());
                }
                this.plugin.clearModuleDescriptor(moduleDescriptor.getKey());
                if (this.plugin.isFrameworkShuttingDown()) continue;
                UnrecognisedModuleDescriptor unrecognisedModuleDescriptor = new UnrecognisedModuleDescriptor();
                Element source = this.plugin.getModuleElements().get(moduleDescriptor.getKey());
                if (source == null) continue;
                unrecognisedModuleDescriptor.init((Plugin)this.plugin, source);
                unrecognisedModuleDescriptor.setErrorText("Support for this module is not currently installed.");
                this.plugin.addModuleDescriptor((ModuleDescriptor)unrecognisedModuleDescriptor);
                if (this.plugin.getPluginState() != PluginState.ENABLED) continue;
                this.pluginEventManager.broadcast((Object)new PluginModuleAvailableEvent((ModuleDescriptor)unrecognisedModuleDescriptor));
            }
        }
    }

    <T extends ModuleDescriptor<?>> List<T> getModuleDescriptorsByDescriptorClass(Class<T> descriptor) {
        ArrayList<T> result = new ArrayList<T>();
        for (ModuleDescriptor moduleDescriptor : this.plugin.getModuleDescriptors()) {
            if (!descriptor.isAssignableFrom(moduleDescriptor.getClass())) continue;
            result.add(descriptor.cast(moduleDescriptor));
        }
        return result;
    }

    private boolean isRestDescriptorNotResolvable(ListableModuleDescriptorFactory factory, Element source) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (source.getName().equals("rest")) {
            Element restMigrationElement = source.getParent().element("rest-migration");
            String descriptorClassName = factory.getModuleDescriptor(source.getName()).getClass().getCanonicalName();
            boolean isNotRestV2Descriptor = restMigrationElement == null && descriptorClassName.equals("com.atlassian.plugins.rest.v2.descriptor.RestModuleDescriptor");
            boolean isNotRestV1Descriptor = restMigrationElement != null && restMigrationElement.elements("rest-v2") != null && descriptorClassName.equals("com.atlassian.plugins.rest.module.RestModuleDescriptor");
            return isNotRestV2Descriptor || isNotRestV1Descriptor;
        }
        return false;
    }
}

