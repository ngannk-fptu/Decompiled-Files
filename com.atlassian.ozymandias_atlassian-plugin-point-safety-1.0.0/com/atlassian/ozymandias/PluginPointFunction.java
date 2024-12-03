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
public interface PluginPointFunction<D extends ModuleDescriptor<MT>, MT, RT> {
    @Nullable
    public RT onModule(@Nonnull D var1, @Nullable MT var2);
}

