/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.instrumentation.operations.OpSnapshot
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.profiling.TimerSnapshot;
import com.atlassian.instrumentation.operations.OpSnapshot;
import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
class AtlassianInstrumentationTimerSnapshot
implements TimerSnapshot {
    private final OpSnapshot snapshot;

    AtlassianInstrumentationTimerSnapshot(OpSnapshot snapshot) {
        this.snapshot = (OpSnapshot)Preconditions.checkNotNull((Object)snapshot);
    }

    @Override
    public @NonNull String getName() {
        return this.snapshot.getName();
    }

    @Override
    public long getInvocationCount() {
        return this.snapshot.getInvocationCount();
    }

    @Override
    public long getElapsedTotalTime(TimeUnit unit) {
        return this.snapshot.getElapsedTotalTime(unit);
    }

    @Override
    public long getElapsedMinTime(TimeUnit unit) {
        return this.snapshot.getElapsedMinTime(unit);
    }

    @Override
    public long getElapsedMaxTime(TimeUnit unit) {
        return this.snapshot.getElapsedMaxTime(unit);
    }

    @Override
    public long getCpuTotalTime(TimeUnit unit) {
        return this.snapshot.getCpuTotalTime(unit);
    }

    @Override
    public long getCpuMinTime(TimeUnit unit) {
        return this.snapshot.getCpuMinTime(unit);
    }

    @Override
    public long getCpuMaxTime(TimeUnit unit) {
        return this.snapshot.getCpuMaxTime(unit);
    }
}

