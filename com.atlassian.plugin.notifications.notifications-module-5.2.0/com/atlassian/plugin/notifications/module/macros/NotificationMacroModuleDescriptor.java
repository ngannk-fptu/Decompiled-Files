/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.plugin.notifications.module.macros;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.notifications.api.macros.Macro;

public class NotificationMacroModuleDescriptor
extends AbstractModuleDescriptor<Macro> {
    public NotificationMacroModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public Macro getModule() {
        return (Macro)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

