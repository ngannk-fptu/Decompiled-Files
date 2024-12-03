/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
public interface TimerSnapshot {
    public @NonNull String getName();

    public long getInvocationCount();

    public long getElapsedTotalTime(TimeUnit var1);

    public long getElapsedMinTime(TimeUnit var1);

    public long getElapsedMaxTime(TimeUnit var1);

    public long getCpuTotalTime(TimeUnit var1);

    public long getCpuMinTime(TimeUnit var1);

    public long getCpuMaxTime(TimeUnit var1);
}

