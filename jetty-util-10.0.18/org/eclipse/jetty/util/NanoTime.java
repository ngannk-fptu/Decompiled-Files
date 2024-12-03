/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.util.concurrent.TimeUnit;

public class NanoTime {
    public static long now() {
        return System.nanoTime();
    }

    public static long elapsed(long beginNanoTime, long endNanoTime) {
        return endNanoTime - beginNanoTime;
    }

    public static long since(long beginNanoTime) {
        return NanoTime.elapsed(beginNanoTime, NanoTime.now());
    }

    public static long until(long endNanoTime) {
        return NanoTime.elapsed(NanoTime.now(), endNanoTime);
    }

    public static long millisElapsed(long beginNanoTime, long endNanoTime) {
        return TimeUnit.NANOSECONDS.toMillis(NanoTime.elapsed(beginNanoTime, endNanoTime));
    }

    public static long millisSince(long beginNanoTime) {
        return NanoTime.millisElapsed(beginNanoTime, NanoTime.now());
    }

    public static long millisUntil(long endNanoTime) {
        return NanoTime.millisElapsed(NanoTime.now(), endNanoTime);
    }

    public static long secondsElapsed(long beginNanoTime, long endNanoTime) {
        return TimeUnit.NANOSECONDS.toSeconds(NanoTime.elapsed(beginNanoTime, endNanoTime));
    }

    public static long secondsSince(long beginNanoTime) {
        return NanoTime.secondsElapsed(beginNanoTime, NanoTime.now());
    }

    public static long secondsUntil(long endNanoTime) {
        return NanoTime.secondsElapsed(NanoTime.now(), endNanoTime);
    }

    public static boolean isBefore(long nanoTime1, long nanoTime2) {
        return nanoTime1 - nanoTime2 < 0L;
    }

    public static boolean isBeforeOrSame(long nanoTime1, long nanoTime2) {
        return nanoTime1 - nanoTime2 <= 0L;
    }

    public static void spinWait(long nanos) {
        long start = NanoTime.now();
        while (NanoTime.since(start) < nanos) {
            Thread.onSpinWait();
        }
    }

    private NanoTime() {
    }
}

