/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  javax.annotation.Nonnull
 */
package com.atlassian.ozymandias;

import com.atlassian.plugin.ModuleDescriptor;
import javax.annotation.Nonnull;

@FunctionalInterface
public interface ModuleDescriptorVisitor<D extends ModuleDescriptor<?>> {
    public void visit(@Nonnull D var1);
}

