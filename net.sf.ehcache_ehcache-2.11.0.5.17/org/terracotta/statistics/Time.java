/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics;

public final class Time {
    private static volatile TimeSource TIME_SOURCE = new TimeSource(){

        @Override
        public long time() {
            return System.nanoTime();
        }

        @Override
        public long absoluteTime() {
            return System.currentTimeMillis();
        }
    };

    private Time() {
    }

    public static long time() {
        return TIME_SOURCE.time();
    }

    public static long absoluteTime() {
        return TIME_SOURCE.absoluteTime();
    }

    public static interface TimeSource {
        public long time();

        public long absoluteTime();
    }
}

