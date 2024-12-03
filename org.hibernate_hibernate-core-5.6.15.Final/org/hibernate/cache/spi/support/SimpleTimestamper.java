/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class SimpleTimestamper {
    private static final int BIN_DIGITS = 12;
    private static final AtomicLong VALUE = new AtomicLong();
    public static final short ONE_MS = 4096;

    public static long next() {
        block0: while (true) {
            long base = System.currentTimeMillis() << 12;
            long maxValue = base + 4096L - 1L;
            long current = VALUE.get();
            long update = Math.max(base, current + 1L);
            while (true) {
                if (update >= maxValue) continue block0;
                if (VALUE.compareAndSet(current, update)) {
                    return update;
                }
                current = VALUE.get();
                update = Math.max(base, current + 1L);
            }
            break;
        }
    }

    public static int timeOut() {
        return (int)TimeUnit.SECONDS.toMillis(60L) * 4096;
    }

    private SimpleTimestamper() {
    }
}

