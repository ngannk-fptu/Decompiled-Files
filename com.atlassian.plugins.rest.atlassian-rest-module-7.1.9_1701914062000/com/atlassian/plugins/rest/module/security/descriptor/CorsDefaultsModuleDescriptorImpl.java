/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.plugins.rest.module.security.descriptor;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaults;
import com.atlassian.plugins.rest.common.security.descriptor.CorsDefaultsModuleDescriptor;

public final class CorsDefaultsModuleDescriptorImpl
extends AbstractModuleDescriptor<CorsDefaults>
implements CorsDefaultsModuleDescriptor {
    public CorsDefaultsModuleDescriptorImpl(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public CorsDefaults getModule() {
        return (CorsDefaults)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

