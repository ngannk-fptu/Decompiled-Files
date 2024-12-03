/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

@Deprecated(forRemoval=true)
public class FeatureModuleDescriptor
extends AbstractModuleDescriptor<Void> {
    public FeatureModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public Void getModule() {
        return null;
    }
}

