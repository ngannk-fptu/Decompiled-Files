/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.mywork.client.plugin;

import com.atlassian.mywork.service.ActionService;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class ActionServicePluginModuleDescriptor
extends AbstractModuleDescriptor<ActionService> {
    public ActionServicePluginModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public ActionService getModule() {
        return (ActionService)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

