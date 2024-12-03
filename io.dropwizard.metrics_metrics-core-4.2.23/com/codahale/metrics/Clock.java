/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

public abstract class Clock {
    public abstract long getTick();

    public long getTime() {
        return System.currentTimeMillis();
    }

    public static Clock defaultClock() {
        return UserTimeClockHolder.DEFAULT;
    }

    private static class UserTimeClockHolder {
        private static final Clock DEFAULT = new UserTimeClock();

        private UserTimeClockHolder() {
        }
    }

    public static class UserTimeClock
    extends Clock {
        @Override
        public long getTick() {
            return System.nanoTime();
        }
    }
}

