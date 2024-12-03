/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.extension.ModelMetadataProvider
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.plugin.descriptor.api;

import com.atlassian.confluence.api.extension.ModelMetadataProvider;
import com.atlassian.confluence.plugin.descriptor.DefaultFactoryModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class ModelMetadataProviderModuleDescriptor
extends DefaultFactoryModuleDescriptor<ModelMetadataProvider> {
    public ModelMetadataProviderModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    @Override
    public ModelMetadataProvider getModule() {
        return (ModelMetadataProvider)this.getModuleFromProvider();
    }
}

