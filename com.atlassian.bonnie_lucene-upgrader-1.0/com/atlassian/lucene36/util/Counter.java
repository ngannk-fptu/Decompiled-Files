/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Counter {
    public abstract long addAndGet(long var1);

    public abstract long get();

    public static Counter newCounter() {
        return Counter.newCounter(false);
    }

    public static Counter newCounter(boolean threadSafe) {
        return threadSafe ? new AtomicCounter() : new SerialCounter();
    }

    private static final class AtomicCounter
    extends Counter {
        private final AtomicLong count = new AtomicLong();

        private AtomicCounter() {
        }

        public long addAndGet(long delta) {
            return this.count.addAndGet(delta);
        }

        public long get() {
            return this.count.get();
        }
    }

    private static final class SerialCounter
    extends Counter {
        private long count = 0L;

        private SerialCounter() {
        }

        public long addAndGet(long delta) {
            return this.count += delta;
        }

        public long get() {
            return this.count;
        }
    }
}

