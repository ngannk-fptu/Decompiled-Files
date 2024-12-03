/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 */
package com.atlassian.util.profiling.strategy;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.ProfilerConfiguration;
import com.atlassian.util.profiling.Ticker;
import javax.annotation.Nonnull;

@Internal
public interface ProfilerStrategy {
    default public void onRequestEnd() {
    }

    public void setConfiguration(@Nonnull ProfilerConfiguration var1);

    @Nonnull
    public Ticker start(@Nonnull String var1);
}

