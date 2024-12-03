/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.plugin.descriptor.restore;

import com.atlassian.confluence.plugin.descriptor.DefaultFactoryModuleDescriptor;
import com.atlassian.confluence.plugin.descriptor.restore.PluginExistingEntityFinder;
import com.atlassian.plugin.module.ModuleFactory;

public class PluginExistingEntityFinderModuleDescriptor
extends DefaultFactoryModuleDescriptor<PluginExistingEntityFinder> {
    public PluginExistingEntityFinderModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    @Override
    public PluginExistingEntityFinder getModule() {
        return (PluginExistingEntityFinder)this.getModuleFromProvider();
    }
}

