/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.plugin.descriptor.macro;

import com.atlassian.confluence.macro.browser.MacroMetadataProvider;
import com.atlassian.confluence.plugin.descriptor.DefaultFactoryModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class MacroMetadataModuleDescriptor
extends DefaultFactoryModuleDescriptor<MacroMetadataProvider> {
    public MacroMetadataModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    @Override
    public MacroMetadataProvider getModule() {
        return (MacroMetadataProvider)this.getModuleFromProvider();
    }
}

