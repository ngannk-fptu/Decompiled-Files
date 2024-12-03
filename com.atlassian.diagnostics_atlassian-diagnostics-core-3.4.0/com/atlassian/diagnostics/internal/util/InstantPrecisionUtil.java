/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.util;

import java.time.Instant;

public class InstantPrecisionUtil {
    private InstantPrecisionUtil() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is a utility class, can't be instantiated");
    }

    public static Instant truncateNanoSecondPrecision(Instant instant) {
        return Instant.ofEpochMilli(instant.toEpochMilli());
    }
}

