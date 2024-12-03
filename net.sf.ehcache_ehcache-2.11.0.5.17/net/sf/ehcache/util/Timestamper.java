/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.util;

import java.util.concurrent.atomic.AtomicLong;
import net.sf.ehcache.util.SlewClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Timestamper {
    public static final int BIN_DIGITS = Integer.getInteger("net.sf.ehcache.util.Timestamper.shift", 12);
    public static final int ONE_MS = 1 << BIN_DIGITS;
    private static final Logger LOG = LoggerFactory.getLogger(Timestamper.class);
    private static final int MAX_LOG = Integer.getInteger("net.sf.ehcache.util.Timestamper.log.max", 1) * 1000;
    private static final AtomicLong VALUE = new AtomicLong();
    private static final AtomicLong LOGGED = new AtomicLong();

    private Timestamper() {
    }

    public static long next() {
        int runs = 0;
        while (true) {
            long base = SlewClock.timeMillis() << BIN_DIGITS;
            long maxValue = base + (long)ONE_MS - 1L;
            long current = VALUE.get();
            long update = Math.max(base, current + 1L);
            while (update < maxValue) {
                if (VALUE.compareAndSet(current, update)) {
                    if (runs > 1) {
                        Timestamper.log(base, "Thread spin-waits on time to pass. Looped {} times, you might want to increase -Dnet.sf.ehcache.util.Timestamper.shift", runs);
                    }
                    return update;
                }
                current = VALUE.get();
                update = Math.max(base, current + 1L);
            }
            ++runs;
        }
    }

    private static void log(long base, String message, Object ... params) {
        if (LOG.isInfoEnabled()) {
            long thisLog = (base >> BIN_DIGITS) / (long)MAX_LOG;
            long previousLog = LOGGED.get();
            if (previousLog != thisLog && LOGGED.compareAndSet(previousLog, thisLog)) {
                LOG.info(message, params);
            }
        }
    }
}

