/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.predicate;

import com.atlassian.plugin.ModuleDescriptor;

@Deprecated
public interface ModuleDescriptorPredicate<T> {
    public boolean matches(ModuleDescriptor<? extends T> var1);
}

