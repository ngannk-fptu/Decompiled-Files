/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.AbstractWebFragmentModuleDescriptor
 *  com.atlassian.util.concurrent.ResettableLazyReference
 */
package com.atlassian.plugins.avatar;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.AbstractWebFragmentModuleDescriptor;
import com.atlassian.plugins.avatar.AvatarProvider;
import com.atlassian.util.concurrent.ResettableLazyReference;

public class AvatarProviderModuleDescriptor
extends AbstractWebFragmentModuleDescriptor<AvatarProvider>
implements Comparable<AvatarProviderModuleDescriptor> {
    private final ResettableLazyReference<AvatarProvider> moduleReference = new ResettableLazyReference<AvatarProvider>(){

        protected AvatarProvider create() throws Exception {
            return AvatarProviderModuleDescriptor.this.createModule();
        }
    };

    public AvatarProviderModuleDescriptor(ModuleFactory moduleCreator, WebInterfaceManager webInterfaceManager) {
        super(moduleCreator, webInterfaceManager);
    }

    private AvatarProvider createModule() {
        AvatarProvider module = (AvatarProvider)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
        return module;
    }

    public AvatarProvider getModule() {
        return (AvatarProvider)this.moduleReference.get();
    }

    public void enabled() {
        super.enabled();
        this.moduleReference.reset();
    }

    public void disabled() {
        super.disabled();
        this.moduleReference.reset();
    }

    @Override
    public int compareTo(AvatarProviderModuleDescriptor otherDescriptor) {
        return otherDescriptor.weight - this.weight;
    }
}

