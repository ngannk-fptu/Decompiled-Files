/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.util;

import java.util.concurrent.atomic.AtomicLong;
import net.sf.ehcache.util.TimeProviderLoader;
import net.sf.ehcache.util.lang.VicariousThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SlewClock {
    private static final Logger LOG = LoggerFactory.getLogger(SlewClock.class);
    private static final TimeProvider PROVIDER = TimeProviderLoader.getTimeProvider();
    private static final long DRIFT_MAXIMAL = Integer.getInteger("net.sf.ehcache.util.Timestamper.drift.max", 50).intValue();
    private static final long SLEEP_MAXIMAL = Integer.getInteger("net.sf.ehcache.util.Timestamper.sleep.max", 50).intValue();
    private static final int SLEEP_BASE = Integer.getInteger("net.sf.ehcache.util.Timestamper.sleep.min", 25);
    private static final AtomicLong CURRENT = new AtomicLong(Long.MIN_VALUE);
    private static final VicariousThreadLocal<Long> OFFSET = new VicariousThreadLocal();

    private SlewClock() {
    }

    @Deprecated
    static void realignWithTimeProvider() {
        CURRENT.set(SlewClock.getCurrentTime());
    }

    static long timeMillis() {
        boolean interrupted = false;
        try {
            while (true) {
                long l;
                long mono = CURRENT.get();
                long wall = SlewClock.getCurrentTime();
                if (wall == mono) {
                    OFFSET.remove();
                    l = wall;
                    return l;
                }
                if (wall > mono) {
                    if (!CURRENT.compareAndSet(mono, wall)) continue;
                    OFFSET.remove();
                    l = wall;
                    return l;
                }
                long delta = mono - wall;
                if (delta < DRIFT_MAXIMAL) {
                    OFFSET.remove();
                    long l2 = mono;
                    return l2;
                }
                Long lastDelta = OFFSET.get();
                if (lastDelta == null || delta < lastDelta) {
                    if (!CURRENT.compareAndSet(mono, mono + 1L)) continue;
                    OFFSET.set(delta);
                    long l3 = mono + 1L;
                    return l3;
                }
                OFFSET.set(Math.max(delta, lastDelta));
                try {
                    long sleep = SlewClock.sleepTime(delta, lastDelta);
                    LOG.trace("{} sleeping for {}ms to adjust for wall-clock drift.", (Object)Thread.currentThread(), (Object)sleep);
                    Thread.sleep(sleep);
                }
                catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static boolean isThreadCatchingUp() {
        return OFFSET.get() != null;
    }

    static long behind() {
        Long offset = OFFSET.get();
        return offset == null ? 0L : offset;
    }

    private static long sleepTime(long current, long previous) {
        long target = (long)SLEEP_BASE + (current - previous) * 2L;
        return Math.min(target > 0L ? target : (long)SLEEP_BASE, SLEEP_MAXIMAL);
    }

    private static long getCurrentTime() {
        return PROVIDER.currentTimeMillis();
    }

    static interface TimeProvider {
        public long currentTimeMillis();
    }
}

