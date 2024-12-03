/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.plugin.servlet.download.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.servlet.DownloadStrategy;

public class DownloadStrategyModuleDescriptor
extends AbstractModuleDescriptor<DownloadStrategy> {
    public DownloadStrategyModuleDescriptor(ModuleFactory moduleCreator) {
        super(moduleCreator);
    }

    public DownloadStrategy getModule() {
        return (DownloadStrategy)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

