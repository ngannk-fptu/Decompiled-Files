/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.diagnostics.internal.platform.plugin;

import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import java.util.Collection;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
public interface PluginFinder {
    public Collection<String> getPluginNamesInCurrentCallStack();

    public Collection<String> getPluginNamesFromStackTrace(StackTraceElement[] var1);

    @Nullable
    default public String getInvokingPluginKeyFromStackTrace(@Nullable StackTraceElement[] stackTrace) {
        return null;
    }

    @Nullable
    default public String getInvokingPluginKeyFromClassContext(@Nullable Class<?>[] classContext) {
        return null;
    }
}

