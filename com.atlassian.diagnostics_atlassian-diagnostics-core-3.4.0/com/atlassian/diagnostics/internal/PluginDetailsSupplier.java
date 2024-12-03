/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.PluginDetails
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.PluginDetails;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PluginDetailsSupplier {
    @Nonnull
    public PluginDetails getPluginDetails(@Nonnull String var1, @Nullable String var2);
}

