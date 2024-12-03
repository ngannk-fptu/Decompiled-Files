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

public final class NoOpModuleDescriptor
extends AbstractModuleDescriptor<Object> {
    public NoOpModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public Object getModule() {
        return null;
    }

    public void enabled() {
    }

    public void disabled() {
    }
}

