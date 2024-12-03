/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.util;

import java.time.Duration;

public final class DurationUtil {
    private DurationUtil() {
    }

    public static String humanReadableDuration(Duration duration) {
        return duration.toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase();
    }
}

