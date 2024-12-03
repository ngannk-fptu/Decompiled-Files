/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.lesscss.spi.UriResolver
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.plugins.less;

import com.atlassian.lesscss.spi.UriResolver;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class UriResolverModuleDescriptor
extends AbstractModuleDescriptor<UriResolver> {
    private UriResolver uriResolver;

    public UriResolverModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void enabled() {
        super.enabled();
        this.uriResolver = (UriResolver)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
        if (this.uriResolver instanceof StateAware) {
            ((StateAware)this.uriResolver).enabled();
        }
    }

    public void disabled() {
        try {
            if (this.uriResolver instanceof StateAware) {
                ((StateAware)this.uriResolver).disabled();
            }
        }
        finally {
            this.uriResolver = null;
            super.disabled();
        }
    }

    public UriResolver getModule() {
        return this.uriResolver;
    }
}

