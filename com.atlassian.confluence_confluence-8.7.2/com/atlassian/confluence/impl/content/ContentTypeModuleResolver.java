/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.impl.content;

import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.ContentTypeModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public interface ContentTypeModuleResolver {
    public Collection<ContentTypeModuleDescriptor> getAllModuleDescriptors();

    default public Optional<ContentType> findContentType(String contentTypeKey) {
        return this.findModuleDescriptor(contentTypeKey).map(ContentTypeModuleDescriptor::getModule);
    }

    default public Optional<ContentTypeModuleDescriptor> findModuleDescriptor(String contentTypeKey) {
        return this.getAllModuleDescriptors().stream().filter(desciptor -> desciptor.getContentType().equals(contentTypeKey)).findFirst();
    }

    public static ContentTypeModuleResolver create(PluginAccessor pluginAccessor) {
        return ContentTypeModuleResolver.create(() -> pluginAccessor.getEnabledModuleDescriptorsByClass(ContentTypeModuleDescriptor.class));
    }

    public static ContentTypeModuleResolver create(Supplier<Collection<ContentTypeModuleDescriptor>> moduleDescriptorSupplier) {
        return moduleDescriptorSupplier::get;
    }
}

