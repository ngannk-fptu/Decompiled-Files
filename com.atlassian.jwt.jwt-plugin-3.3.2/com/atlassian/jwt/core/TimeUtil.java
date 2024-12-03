/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.core;

public class TimeUtil {
    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

    public static long currentTimePlusNSeconds(long n) {
        return TimeUtil.currentTimeSeconds() + n;
    }
}

