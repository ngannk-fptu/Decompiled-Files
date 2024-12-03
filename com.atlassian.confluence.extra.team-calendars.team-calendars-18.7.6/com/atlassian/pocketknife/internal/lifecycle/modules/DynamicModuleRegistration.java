/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.StateAware
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Service
 */
package com.atlassian.pocketknife.internal.lifecycle.modules;

import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.StateAware;
import com.atlassian.pocketknife.api.lifecycle.modules.ModuleRegistrationHandle;
import com.atlassian.pocketknife.internal.lifecycle.modules.GhettoCode;
import com.atlassian.pocketknife.internal.lifecycle.modules.Kit;
import com.atlassian.pocketknife.internal.lifecycle.modules.utils.BundleUtil;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DynamicModuleRegistration {
    private static final Logger log = LoggerFactory.getLogger(DynamicModuleRegistration.class);
    private final BundleContext bundleContext;

    @Autowired
    public DynamicModuleRegistration(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public ModuleRegistrationHandle registerDescriptors(Plugin plugin, Iterable<ModuleDescriptor> descriptors) {
        String pluginId = Kit.pluginIdentifier(plugin);
        Bundle bundle = BundleUtil.findBundleForPlugin(this.bundleContext, plugin.getKey());
        BundleContext targetBundleContext = bundle.getBundleContext();
        ArrayList registrations = Lists.newArrayList();
        for (ModuleDescriptor descriptor : descriptors) {
            String moduleIdentifier = Kit.getModuleIdentifier(descriptor);
            log.debug("Registering module '{}' of type '{}' into plugin '{}'", new Object[]{moduleIdentifier, descriptor.getClass().getSimpleName(), pluginId});
            ModuleDescriptor existingDescriptor = plugin.getModuleDescriptor(descriptor.getKey());
            if (existingDescriptor != null) {
                log.error("Duplicate key '{}' detected in plugin '{}'; disabling previous instance", (Object)moduleIdentifier, (Object)pluginId);
                ((StateAware)existingDescriptor).disabled();
            }
            ServiceRegistration serviceRegistration = this.registerModule(targetBundleContext, descriptor);
            registrations.add(new TrackedDynamicModule(serviceRegistration, descriptor));
        }
        return new ModuleRegistrationHandleImpl(registrations);
    }

    private ServiceRegistration registerModule(BundleContext targetBundleContext, ModuleDescriptor<?> descriptor) {
        return targetBundleContext.registerService(ModuleDescriptor.class.getName(), descriptor, null);
    }

    static class ModuleRegistrationHandleImpl
    implements ModuleRegistrationHandle {
        private final List<TrackedDynamicModule> registrations;
        private final List<ModuleRegistrationHandle> theOthers;

        ModuleRegistrationHandleImpl(List<TrackedDynamicModule> registrations) {
            this(registrations, Collections.emptyList());
        }

        ModuleRegistrationHandleImpl(List<TrackedDynamicModule> registrations, List<ModuleRegistrationHandle> theOthers) {
            this.registrations = registrations;
            this.theOthers = theOthers;
        }

        @Override
        public void unregister() {
            for (ModuleRegistrationHandle theOther : this.theOthers) {
                theOther.unregister();
            }
            for (TrackedDynamicModule reg : this.registrations) {
                reg.unregister();
            }
            this.registrations.clear();
            this.theOthers.clear();
        }

        @Override
        public Iterable<ModuleCompleteKey> getModules() {
            ArrayList<ModuleCompleteKey> keys = new ArrayList<ModuleCompleteKey>();
            if (this.theOthers != null) {
                for (ModuleRegistrationHandle handle : this.theOthers) {
                    Iterables.addAll(keys, handle.getModules());
                }
            }
            for (TrackedDynamicModule module : this.registrations) {
                keys.add(module.getModuleCompleteKey());
            }
            return keys;
        }

        @Override
        public ModuleRegistrationHandle union(ModuleRegistrationHandle other) {
            return new ModuleRegistrationHandleImpl(this.registrations, this.smoosh(other));
        }

        private List<ModuleRegistrationHandle> smoosh(ModuleRegistrationHandle other) {
            if (other == this) {
                return this.theOthers;
            }
            ArrayList list = Lists.newArrayList();
            list.addAll(this.theOthers);
            list.add(other);
            return list;
        }
    }

    static class TrackedDynamicModule {
        private final ServiceRegistration serviceRegistration;
        private final ModuleDescriptor<?> moduleDescriptor;
        private final ModuleCompleteKey moduleCompleteKey;

        TrackedDynamicModule(ServiceRegistration serviceRegistration, ModuleDescriptor<?> moduleDescriptor) {
            this.serviceRegistration = serviceRegistration;
            this.moduleDescriptor = moduleDescriptor;
            this.moduleCompleteKey = new ModuleCompleteKey(moduleDescriptor.getCompleteKey());
        }

        void unregister() {
            try {
                String moduleIdentifier = Kit.getModuleIdentifier(this.moduleDescriptor);
                log.debug("Un-registering module '{}' of type '{}' ", (Object)moduleIdentifier, (Object)this.moduleDescriptor.getClass().getSimpleName());
                this.serviceRegistration.unregister();
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
            GhettoCode.removeModuleDescriptorElement(this.moduleDescriptor.getPlugin(), this.moduleDescriptor.getKey());
        }

        public ModuleCompleteKey getModuleCompleteKey() {
            return this.moduleCompleteKey;
        }
    }
}

