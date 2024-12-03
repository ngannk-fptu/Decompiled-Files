/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.util;

import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nonnull;

public final class DateDifferenceUtil {
    private DateDifferenceUtil() {
    }

    public static long absoluteDifferenceInDays(@Nonnull Instant firstInstant, @Nonnull Instant secondInstant) {
        return Math.abs(Duration.between(firstInstant, secondInstant).toDays());
    }
}

