/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.ChainModuleDescriptorFactory
 *  com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  com.atlassian.sal.api.component.ComponentLocator
 *  com.google.common.annotations.VisibleForTesting
 *  org.osgi.util.tracker.ServiceTracker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.stereotype.Service
 */
package com.atlassian.pocketknife.internal.lifecycle.modules;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.ChainModuleDescriptorFactory;
import com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.sal.api.component.ComponentLocator;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

@Service
public class CombinedModuleDescriptorFactoryProvider
implements DisposableBean {
    public ModuleDescriptorFactory getModuleDescriptorFactory() {
        return this.getChainedModuleDescriptorFactory(this.getHostModuleDescriptoryFactory());
    }

    public void destroy() throws Exception {
    }

    @VisibleForTesting
    ModuleDescriptorFactory getHostModuleDescriptoryFactory() {
        return (ModuleDescriptorFactory)ComponentLocator.getComponent(ModuleDescriptorFactory.class);
    }

    @VisibleForTesting
    OsgiContainerManager getOsgiContainerManager() {
        return (OsgiContainerManager)ComponentLocator.getComponent(OsgiContainerManager.class);
    }

    private ModuleDescriptorFactory getChainedModuleDescriptorFactory(ModuleDescriptorFactory originalFactory) {
        ModuleDescriptorFactory dynFactory;
        OsgiContainerManager osgi = this.getOsgiContainerManager();
        ServiceTracker moduleDescriptorFactoryTracker = osgi.getServiceTracker(ModuleDescriptorFactory.class.getName());
        ServiceTracker listableModuleDescriptorFactoryTracker = osgi.getServiceTracker(ListableModuleDescriptorFactory.class.getName());
        ArrayList<ModuleDescriptorFactory> factories = new ArrayList<ModuleDescriptorFactory>();
        factories.add(originalFactory);
        Object[] serviceObjs = moduleDescriptorFactoryTracker.getServices();
        if (serviceObjs != null) {
            for (Object fac : serviceObjs) {
                dynFactory = (ModuleDescriptorFactory)fac;
                factories.add(dynFactory);
            }
        }
        if ((serviceObjs = listableModuleDescriptorFactoryTracker.getServices()) != null) {
            for (Object fac : serviceObjs) {
                dynFactory = (ModuleDescriptorFactory)fac;
                if (factories.contains(dynFactory)) continue;
                factories.add(dynFactory);
            }
        }
        moduleDescriptorFactoryTracker.close();
        listableModuleDescriptorFactoryTracker.close();
        factories.add(new UnrecognisedModuleDescriptorFallbackFactory());
        return new ChainModuleDescriptorFactory(factories);
    }

    static class UnrecognisedModuleDescriptorFallbackFactory
    implements ModuleDescriptorFactory {
        private static final Logger log = LoggerFactory.getLogger(UnrecognisedModuleDescriptorFallbackFactory.class);
        public static final String DESCRIPTOR_TEXT = "Support for this module is not currently installed.";

        UnrecognisedModuleDescriptorFallbackFactory() {
        }

        public UnrecognisedModuleDescriptor getModuleDescriptor(String type) throws PluginParseException, IllegalAccessException, InstantiationException, ClassNotFoundException {
            log.info("Unknown module descriptor of type " + type + " registered as an unrecognised descriptor.");
            UnrecognisedModuleDescriptor descriptor = new UnrecognisedModuleDescriptor();
            descriptor.setErrorText(DESCRIPTOR_TEXT);
            return descriptor;
        }

        public boolean hasModuleDescriptor(String type) {
            return true;
        }

        public Class<? extends ModuleDescriptor<?>> getModuleDescriptorClass(String type) {
            return UnrecognisedModuleDescriptor.class;
        }
    }
}

