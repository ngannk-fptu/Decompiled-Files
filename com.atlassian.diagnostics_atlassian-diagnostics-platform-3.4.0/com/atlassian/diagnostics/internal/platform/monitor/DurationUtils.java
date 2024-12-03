/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor;

import java.time.Duration;
import javax.annotation.Nonnull;

public class DurationUtils {
    private final Duration duration;

    private DurationUtils(@Nonnull Duration duration) {
        this.duration = duration;
    }

    public static DurationUtils durationOfMillis(@Nonnull long duration) {
        return DurationUtils.durationOf(Duration.ofMillis(duration));
    }

    public static DurationUtils durationOf(@Nonnull Duration duration) {
        return new DurationUtils(duration);
    }

    public boolean isGreaterThan(@Nonnull Duration otherDuration) {
        return this.duration.compareTo(otherDuration) > 0;
    }

    public boolean isGreaterThanOrEqualTo(@Nonnull Duration otherDuration) {
        return this.duration.compareTo(otherDuration) >= 0;
    }

    public boolean isSameAs(@Nonnull Duration otherDuration) {
        return this.duration.compareTo(otherDuration) == 0;
    }

    public boolean isLessThan(@Nonnull Duration otherDuration) {
        return this.duration.compareTo(otherDuration) < 0;
    }

    public boolean isLessThanOrEqualTo(@Nonnull Duration otherDuration) {
        return this.duration.compareTo(otherDuration) <= 0;
    }
}

