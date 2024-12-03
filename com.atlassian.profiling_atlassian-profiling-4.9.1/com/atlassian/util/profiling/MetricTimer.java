/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.Ticker;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Internal
public interface MetricTimer {
    @Nonnull
    public Ticker start();

    default public void update(long time, TimeUnit timeUnit) {
        this.update(Duration.ofNanos(timeUnit.toNanos(time)));
    }

    public void update(Duration var1);
}

