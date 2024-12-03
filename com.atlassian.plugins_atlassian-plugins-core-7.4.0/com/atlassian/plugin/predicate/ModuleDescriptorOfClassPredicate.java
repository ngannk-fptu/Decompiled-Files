/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.util.Assertions
 */
package com.atlassian.plugin.predicate;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.util.Assertions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public class ModuleDescriptorOfClassPredicate<T>
implements Predicate<ModuleDescriptor<T>> {
    private final Collection<Class<? extends ModuleDescriptor<? extends T>>> moduleDescriptorClasses;

    public ModuleDescriptorOfClassPredicate(Class<? extends ModuleDescriptor<? extends T>> moduleDescriptorClass) {
        this.moduleDescriptorClasses = Collections.singleton(moduleDescriptorClass);
    }

    public ModuleDescriptorOfClassPredicate(Class<? extends ModuleDescriptor<? extends T>>[] moduleDescriptorClasses) {
        Assertions.notNull((String)"moduleDescriptorClasses", moduleDescriptorClasses);
        this.moduleDescriptorClasses = Arrays.asList(moduleDescriptorClasses);
    }

    @Override
    public boolean test(ModuleDescriptor<T> moduleDescriptor) {
        return moduleDescriptor != null && this.moduleDescriptorClasses != null && this.moduleDescriptorClasses.stream().anyMatch(descriptorClass -> descriptorClass != null && descriptorClass.isInstance(moduleDescriptor));
    }
}

