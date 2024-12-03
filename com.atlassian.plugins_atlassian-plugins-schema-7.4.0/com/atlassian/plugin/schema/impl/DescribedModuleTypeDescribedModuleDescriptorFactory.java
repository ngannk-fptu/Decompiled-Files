/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.schema.impl;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.schema.descriptor.DescribedModuleDescriptorFactory;
import com.atlassian.plugin.schema.spi.Schema;
import com.atlassian.plugin.schema.spi.SchemaFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;

final class DescribedModuleTypeDescribedModuleDescriptorFactory<T extends ModuleDescriptor<?>>
implements DescribedModuleDescriptorFactory {
    private final ContainerManagedPlugin plugin;
    private final String type;
    private final Iterable<String> typeList;
    private final Class<T> moduleDescriptorClass;
    private final SchemaFactory schemaFactory;

    DescribedModuleTypeDescribedModuleDescriptorFactory(ContainerManagedPlugin plugin, String type, Class<T> moduleDescriptorClass, SchemaFactory schemaFactory) {
        this.plugin = (ContainerManagedPlugin)Preconditions.checkNotNull((Object)plugin);
        this.moduleDescriptorClass = moduleDescriptorClass;
        this.type = type;
        this.schemaFactory = schemaFactory;
        this.typeList = Collections.singleton(type);
    }

    public ModuleDescriptor getModuleDescriptor(String type) {
        ModuleDescriptor result = null;
        if (this.type.equals(type)) {
            result = (ModuleDescriptor)this.plugin.getContainerAccessor().createBean(this.moduleDescriptorClass);
        }
        return result;
    }

    public boolean hasModuleDescriptor(String type) {
        return this.type.equals(type);
    }

    @Override
    @Nullable
    public Schema getSchema(String type) {
        return this.type.equals(type) ? this.schemaFactory.getSchema() : null;
    }

    public Iterable<String> getModuleDescriptorKeys() {
        return this.typeList;
    }

    public Class<? extends ModuleDescriptor<?>> getModuleDescriptorClass(String type) {
        return this.type.equals(type) ? this.moduleDescriptorClass : null;
    }

    public Set<Class<? extends ModuleDescriptor>> getModuleDescriptorClasses() {
        return ImmutableSet.of(this.moduleDescriptorClass);
    }
}

