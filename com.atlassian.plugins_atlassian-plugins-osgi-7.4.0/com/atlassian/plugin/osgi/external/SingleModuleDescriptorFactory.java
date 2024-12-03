/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.plugin.osgi.external;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class SingleModuleDescriptorFactory<T extends ModuleDescriptor>
implements ListableModuleDescriptorFactory {
    private final String type;
    private final Class<T> moduleDescriptorClass;
    private final HostContainer hostContainer;

    public SingleModuleDescriptorFactory(HostContainer hostContainer, String type, Class<T> moduleDescriptorClass) {
        this.moduleDescriptorClass = moduleDescriptorClass;
        this.type = type;
        this.hostContainer = hostContainer;
    }

    public ModuleDescriptor getModuleDescriptor(String type) {
        ModuleDescriptor result = null;
        if (this.type.equals(type)) {
            result = (ModuleDescriptor)this.hostContainer.create(this.moduleDescriptorClass);
        }
        return result;
    }

    public boolean hasModuleDescriptor(String type) {
        return this.type.equals(type);
    }

    public Class<? extends ModuleDescriptor> getModuleDescriptorClass(String type) {
        return this.type.equals(type) ? this.moduleDescriptorClass : null;
    }

    @Override
    public Iterable<String> getModuleDescriptorKeys() {
        return ImmutableSet.of((Object)this.type);
    }

    @Override
    public Set<Class<? extends ModuleDescriptor>> getModuleDescriptorClasses() {
        return ImmutableSet.of(this.moduleDescriptorClass);
    }

    public HostContainer getHostContainer() {
        return this.hostContainer;
    }
}

