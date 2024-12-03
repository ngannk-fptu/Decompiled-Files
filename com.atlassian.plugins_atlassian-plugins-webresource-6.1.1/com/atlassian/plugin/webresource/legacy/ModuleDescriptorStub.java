/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;

public class ModuleDescriptorStub
extends AbstractModuleDescriptor<Void> {
    private String completeKey;

    public ModuleDescriptorStub(WebResourceModuleDescriptor descriptor) {
        super(ModuleFactory.LEGACY_MODULE_FACTORY);
        this.completeKey = descriptor.getCompleteKey();
    }

    public ModuleDescriptorStub(String moduleKey) {
        super(ModuleFactory.LEGACY_MODULE_FACTORY);
        this.completeKey = moduleKey;
    }

    public String getCompleteKey() {
        return this.completeKey;
    }

    public Void getModule() {
        throw new UnsupportedOperationException("Not implemented");
    }
}

