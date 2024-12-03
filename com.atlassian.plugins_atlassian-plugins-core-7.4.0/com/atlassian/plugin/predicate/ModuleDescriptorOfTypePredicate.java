/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptorFactory
 */
package com.atlassian.plugin.predicate;

import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.predicate.ModuleDescriptorOfClassPredicate;

public class ModuleDescriptorOfTypePredicate<M>
extends ModuleDescriptorOfClassPredicate<M> {
    public ModuleDescriptorOfTypePredicate(ModuleDescriptorFactory moduleDescriptorFactory, String moduleDescriptorType) {
        super(moduleDescriptorFactory.getModuleDescriptorClass(moduleDescriptorType));
    }
}

