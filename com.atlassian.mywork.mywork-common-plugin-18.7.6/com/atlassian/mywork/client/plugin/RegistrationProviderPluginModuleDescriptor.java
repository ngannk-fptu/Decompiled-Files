/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.mywork.client.plugin;

import com.atlassian.mywork.service.RegistrationProvider;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class RegistrationProviderPluginModuleDescriptor
extends AbstractModuleDescriptor<RegistrationProvider> {
    public RegistrationProviderPluginModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public RegistrationProvider getModule() {
        return (RegistrationProvider)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

