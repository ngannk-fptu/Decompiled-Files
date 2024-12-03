/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import java.util.concurrent.TimeUnit;

public interface TimeMeter {
    public static final TimeMeter SYSTEM_NANOTIME = new TimeMeter(){

        @Override
        public long currentTimeNanos() {
            return System.nanoTime();
        }

        public String toString() {
            return "SYSTEM_NANOTIME";
        }
    };
    public static final TimeMeter SYSTEM_MILLISECONDS = new TimeMeter(){

        @Override
        public long currentTimeNanos() {
            long nowMillis = System.currentTimeMillis();
            return TimeUnit.MILLISECONDS.toNanos(nowMillis);
        }

        public String toString() {
            return "SYSTEM_MILLISECONDS";
        }
    };

    public long currentTimeNanos();
}

