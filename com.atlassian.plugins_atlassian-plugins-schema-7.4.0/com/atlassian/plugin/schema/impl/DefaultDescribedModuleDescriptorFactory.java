/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.DefaultModuleDescriptorFactory
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Permissions
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.util.resource.AlternativeClassLoaderResourceLoader
 *  com.atlassian.plugin.util.resource.AlternativeResourceLoader
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.plugin.schema.impl;

import com.atlassian.plugin.DefaultModuleDescriptorFactory;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Permissions;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.schema.descriptor.DescribedModuleDescriptorFactory;
import com.atlassian.plugin.schema.impl.DescribedModuleTypeModuleDescriptor;
import com.atlassian.plugin.schema.spi.DocumentBasedSchema;
import com.atlassian.plugin.schema.spi.Schema;
import com.atlassian.plugin.util.resource.AlternativeClassLoaderResourceLoader;
import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class DefaultDescribedModuleDescriptorFactory
extends DefaultModuleDescriptorFactory
implements DescribedModuleDescriptorFactory {
    public DefaultDescribedModuleDescriptorFactory(HostContainer hostContainer) {
        super(hostContainer);
        this.addModuleDescriptor("described-module-type", DescribedModuleTypeModuleDescriptor.class);
    }

    public final Iterable<String> getModuleDescriptorKeys() {
        return ImmutableSet.copyOf(this.getDescriptorClassesMap().keySet());
    }

    public final Set<Class<? extends ModuleDescriptor>> getModuleDescriptorClasses() {
        return ImmutableSet.copyOf(this.getDescriptorClassesMap().values());
    }

    @Override
    public final Schema getSchema(String type) {
        if (!this.getDescriptorClassesMap().containsKey(type)) {
            return null;
        }
        DocumentBasedSchema.DynamicSchemaBuilder builder = DocumentBasedSchema.builder(type).setResourceLoader((AlternativeResourceLoader)new AlternativeClassLoaderResourceLoader(this.getClass())).setRequiredPermissions(Permissions.getRequiredPermissions((Class)this.getModuleDescriptorClass(type)));
        return builder.validate() ? builder.build() : null;
    }
}

