/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.util.ExceptionUtil;

public final class Clock {
    private static final ClockImpl CLOCK = Clock.createClock();

    private Clock() {
    }

    public static long currentTimeMillis() {
        return CLOCK.currentTimeMillis();
    }

    static ClockImpl createClock() {
        String clockImplClassName = System.getProperty("com.hazelcast.clock.impl");
        if (clockImplClassName != null) {
            try {
                return (ClockImpl)ClassLoaderUtil.newInstance(null, clockImplClassName);
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        String clockOffset = System.getProperty("com.hazelcast.clock.offset");
        long offset = 0L;
        if (clockOffset != null) {
            try {
                offset = Long.parseLong(clockOffset);
            }
            catch (NumberFormatException e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        if (offset != 0L) {
            return new SystemOffsetClock(offset);
        }
        return new SystemClock();
    }

    static final class SystemOffsetClock
    extends ClockImpl {
        private final long offset;

        SystemOffsetClock(long offset) {
            this.offset = offset;
        }

        @Override
        protected long currentTimeMillis() {
            return System.currentTimeMillis() + this.offset;
        }
    }

    static final class SystemClock
    extends ClockImpl {
        SystemClock() {
        }

        @Override
        protected long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }

    public static abstract class ClockImpl {
        protected abstract long currentTimeMillis();
    }
}

