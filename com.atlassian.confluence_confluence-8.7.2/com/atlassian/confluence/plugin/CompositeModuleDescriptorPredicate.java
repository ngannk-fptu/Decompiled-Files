/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 */
package com.atlassian.confluence.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class CompositeModuleDescriptorPredicate<T>
implements ModuleDescriptorPredicate<T> {
    private final List<ModuleDescriptorPredicate<T>> predicates;

    public CompositeModuleDescriptorPredicate(ModuleDescriptorPredicate<T> ... predicates) {
        this(Arrays.asList(predicates));
    }

    public CompositeModuleDescriptorPredicate(List<ModuleDescriptorPredicate<T>> predicates) {
        this.predicates = new ArrayList<ModuleDescriptorPredicate<T>>(predicates);
        this.sanityCheckPredicates();
    }

    public boolean matches(ModuleDescriptor<? extends T> moduleDescriptor) {
        for (ModuleDescriptorPredicate<T> predicate : this.predicates) {
            if (predicate.matches(moduleDescriptor)) continue;
            return false;
        }
        return true;
    }

    private void sanityCheckPredicates() {
        if (this.predicates.isEmpty()) {
            throw new IllegalArgumentException("Empty predicate list");
        }
    }
}

