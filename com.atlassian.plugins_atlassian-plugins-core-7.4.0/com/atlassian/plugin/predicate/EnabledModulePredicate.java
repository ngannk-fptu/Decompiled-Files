/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 */
package com.atlassian.plugin.predicate;

import com.atlassian.plugin.ModuleDescriptor;
import java.util.function.Predicate;

public class EnabledModulePredicate
implements Predicate<ModuleDescriptor<?>> {
    @Override
    public boolean test(ModuleDescriptor<?> moduleDescriptor) {
        return moduleDescriptor.isEnabled() && !moduleDescriptor.isBroken();
    }
}

