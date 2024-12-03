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
import java.util.function.Predicate;

public class ModuleOfClassPredicate<T>
implements Predicate<ModuleDescriptor<T>> {
    private final Class<T> moduleClass;

    public ModuleOfClassPredicate(Class<T> moduleClass) {
        this.moduleClass = (Class)Assertions.notNull((String)"moduleClass", moduleClass);
    }

    @Override
    public boolean test(ModuleDescriptor<T> moduleDescriptor) {
        if (moduleDescriptor != null) {
            Class moduleClassInDescriptor = moduleDescriptor.getModuleClass();
            return moduleClassInDescriptor != null && this.moduleClass.isAssignableFrom(moduleClassInDescriptor);
        }
        return false;
    }
}

