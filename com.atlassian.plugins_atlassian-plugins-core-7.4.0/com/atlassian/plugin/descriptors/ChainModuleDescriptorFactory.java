/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 */
package com.atlassian.plugin.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import java.util.Collection;

public class ChainModuleDescriptorFactory
implements ModuleDescriptorFactory {
    private final ModuleDescriptorFactory[] factories;

    public ChainModuleDescriptorFactory(ModuleDescriptorFactory ... factories) {
        this.factories = factories;
    }

    public ChainModuleDescriptorFactory(Collection<? extends ModuleDescriptorFactory> factories) {
        this(factories.toArray(new ModuleDescriptorFactory[factories.size()]));
    }

    public ModuleDescriptor<?> getModuleDescriptor(String type) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        for (ModuleDescriptorFactory factory : this.factories) {
            if (!factory.hasModuleDescriptor(type)) continue;
            return factory.getModuleDescriptor(type);
        }
        return null;
    }

    public boolean hasModuleDescriptor(String type) {
        for (ModuleDescriptorFactory factory : this.factories) {
            if (!factory.hasModuleDescriptor(type)) continue;
            return true;
        }
        return false;
    }

    public Class<? extends ModuleDescriptor> getModuleDescriptorClass(String type) {
        for (ModuleDescriptorFactory factory : this.factories) {
            Class descriptorClass = factory.getModuleDescriptorClass(type);
            if (descriptorClass == null) continue;
            return descriptorClass;
        }
        return null;
    }
}

