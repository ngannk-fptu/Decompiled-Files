/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.ozymandias;

import com.atlassian.plugin.ModuleDescriptor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@FunctionalInterface
public interface PluginPointVisitor<D extends ModuleDescriptor<MT>, MT> {
    public void visit(@Nonnull D var1, @Nullable MT var2);
}

