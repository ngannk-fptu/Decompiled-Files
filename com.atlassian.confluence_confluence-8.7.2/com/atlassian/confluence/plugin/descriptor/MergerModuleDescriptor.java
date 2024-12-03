/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.util.diffs.Merger;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class MergerModuleDescriptor
extends AbstractModuleDescriptor<Merger> {
    public MergerModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public Merger getModule() {
        return (Merger)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

