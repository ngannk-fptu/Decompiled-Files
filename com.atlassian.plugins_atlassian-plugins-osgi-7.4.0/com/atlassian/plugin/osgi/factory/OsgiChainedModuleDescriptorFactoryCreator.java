/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.DefaultModuleDescriptorFactory
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.descriptors.ChainModuleDescriptorFactory
 *  com.atlassian.plugin.hostcontainer.DefaultHostContainer
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.DefaultModuleDescriptorFactory;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.descriptors.ChainModuleDescriptorFactory;
import com.atlassian.plugin.hostcontainer.DefaultHostContainer;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.factory.UnavailableModuleDescriptorRequiringRestartFallbackFactory;
import com.atlassian.plugin.osgi.factory.UnrecognisedModuleDescriptorFallbackFactory;
import com.atlassian.plugin.osgi.factory.descriptor.ComponentImportModuleDescriptor;
import com.atlassian.plugin.osgi.factory.descriptor.ComponentModuleDescriptor;
import com.atlassian.plugin.osgi.factory.descriptor.ModuleTypeModuleDescriptor;
import java.util.ArrayList;
import org.osgi.util.tracker.ServiceTracker;

public class OsgiChainedModuleDescriptorFactoryCreator {
    private final ServiceTrackerFactory serviceTrackerFactory;
    private final ModuleDescriptorFactory transformedDescriptorFactory = new DefaultModuleDescriptorFactory((HostContainer)new DefaultHostContainer()){
        {
            this.addModuleDescriptor("component", ComponentModuleDescriptor.class);
            this.addModuleDescriptor("component-import", ComponentImportModuleDescriptor.class);
            this.addModuleDescriptor("module-type", ModuleTypeModuleDescriptor.class);
        }
    };
    private volatile ServiceTracker moduleDescriptorFactoryTracker;

    public OsgiChainedModuleDescriptorFactoryCreator(ServiceTrackerFactory serviceTrackerFactory) {
        this.serviceTrackerFactory = serviceTrackerFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ModuleDescriptorFactory create(ResourceLocator resourceLocator, ModuleDescriptorFactory originalModuleDescriptorFactory) {
        OsgiChainedModuleDescriptorFactoryCreator osgiChainedModuleDescriptorFactoryCreator = this;
        synchronized (osgiChainedModuleDescriptorFactoryCreator) {
            if (this.moduleDescriptorFactoryTracker == null) {
                this.moduleDescriptorFactoryTracker = this.serviceTrackerFactory.create(ModuleDescriptorFactory.class.getName());
            }
        }
        ArrayList<Object> factories = new ArrayList<Object>();
        factories.add(this.transformedDescriptorFactory);
        factories.add(originalModuleDescriptorFactory);
        Object[] serviceObjs = this.moduleDescriptorFactoryTracker.getServices();
        ArrayList<UnavailableModuleDescriptorRequiringRestartFallbackFactory> wrappedListable = new ArrayList<UnavailableModuleDescriptorRequiringRestartFallbackFactory>();
        if (serviceObjs != null) {
            block3: for (Object fac : serviceObjs) {
                ModuleDescriptorFactory dynFactory = (ModuleDescriptorFactory)fac;
                if (!(dynFactory instanceof ListableModuleDescriptorFactory)) {
                    factories.add((ModuleDescriptorFactory)fac);
                    continue;
                }
                for (Class<? extends ModuleDescriptor> descriptor : ((ListableModuleDescriptorFactory)dynFactory).getModuleDescriptorClasses()) {
                    if (resourceLocator.doesResourceExist(descriptor.getName().replace('.', '/') + ".class")) continue;
                    wrappedListable.add(new UnavailableModuleDescriptorRequiringRestartFallbackFactory((ModuleDescriptorFactory)fac));
                    continue block3;
                }
            }
        }
        factories.add(new ChainModuleDescriptorFactory(wrappedListable));
        factories.add(new UnrecognisedModuleDescriptorFallbackFactory());
        return new ChainModuleDescriptorFactory(factories);
    }

    public static interface ResourceLocator {
        public boolean doesResourceExist(String var1);
    }

    public static interface ServiceTrackerFactory {
        public ServiceTracker create(String var1);
    }
}

