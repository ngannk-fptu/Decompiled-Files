/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.jsr166e;

import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import org.terracotta.statistics.util.VicariousThreadLocal;

abstract class Striped64
extends Number {
    static final ThreadHashCode threadHashCode = new ThreadHashCode();
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    volatile transient Cell[] cells;
    volatile transient long base;
    volatile transient int busy;
    static final AtomicLongFieldUpdater<Striped64> BASE_UPDATER = AtomicLongFieldUpdater.newUpdater(Striped64.class, "base");
    static final AtomicIntegerFieldUpdater<Striped64> BUSY_UPDATER = AtomicIntegerFieldUpdater.newUpdater(Striped64.class, "busy");

    Striped64() {
    }

    final boolean casBase(long cmp, long val) {
        return BASE_UPDATER.compareAndSet(this, cmp, val);
    }

    final boolean casBusy() {
        return BUSY_UPDATER.compareAndSet(this, 0, 1);
    }

    abstract long fn(long var1, long var3);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void retryUpdate(long x, HashCode hc, boolean wasUncontended) {
        int h = hc.code;
        boolean collide = false;
        while (true) {
            long v;
            int n;
            Cell[] as = this.cells;
            if (this.cells != null && (n = as.length) > 0) {
                Cell a = as[n - 1 & h];
                if (a == null) {
                    if (this.busy == 0) {
                        Cell r = new Cell(x);
                        if (this.busy == 0 && this.casBusy()) {
                            boolean created = false;
                            try {
                                int j;
                                int m;
                                Cell[] rs = this.cells;
                                if (this.cells != null && (m = rs.length) > 0 && rs[j = m - 1 & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            }
                            finally {
                                BUSY_UPDATER.set(this, 0);
                            }
                            if (!created) continue;
                            break;
                        }
                    }
                    collide = false;
                } else if (!wasUncontended) {
                    wasUncontended = true;
                } else {
                    v = a.value;
                    if (a.cas(v, this.fn(v, x))) break;
                    if (n >= NCPU || this.cells != as) {
                        collide = false;
                    } else if (!collide) {
                        collide = true;
                    } else if (this.busy == 0 && this.casBusy()) {
                        try {
                            if (this.cells == as) {
                                Cell[] rs = new Cell[n << 1];
                                for (int i = 0; i < n; ++i) {
                                    rs[i] = as[i];
                                }
                                this.cells = rs;
                            }
                        }
                        finally {
                            BUSY_UPDATER.set(this, 0);
                        }
                        collide = false;
                        continue;
                    }
                }
                h ^= h << 13;
                h ^= h >>> 17;
                h ^= h << 5;
                continue;
            }
            if (this.busy == 0 && this.cells == as && this.casBusy()) {
                boolean init = false;
                try {
                    if (this.cells == as) {
                        Cell[] rs = new Cell[2];
                        rs[h & 1] = new Cell(x);
                        this.cells = rs;
                        init = true;
                    }
                }
                finally {
                    BUSY_UPDATER.set(this, 0);
                }
                if (!init) continue;
                break;
            }
            v = this.base;
            if (this.casBase(v, this.fn(v, x))) break;
        }
        hc.code = h;
    }

    final void internalReset(long initialValue) {
        Cell[] as = this.cells;
        BASE_UPDATER.set(this, initialValue);
        if (as != null) {
            for (Cell a : as) {
                if (a == null) continue;
                a.value = initialValue;
            }
        }
    }

    static final class ThreadHashCode
    extends VicariousThreadLocal<HashCode> {
        ThreadHashCode() {
        }

        @Override
        public HashCode initialValue() {
            return new HashCode();
        }
    }

    static final class HashCode {
        static final Random rng = new Random();
        int code;

        HashCode() {
            int h = rng.nextInt();
            this.code = h == 0 ? 1 : h;
        }
    }

    static final class Cell {
        volatile long p0;
        volatile long p1;
        volatile long p2;
        volatile long p3;
        volatile long p4;
        volatile long p5;
        volatile long p6;
        volatile long value;
        volatile long q0;
        volatile long q1;
        volatile long q2;
        volatile long q3;
        volatile long q4;
        volatile long q5;
        volatile long q6;
        private static final AtomicLongFieldUpdater<Cell> VALUE_UPDATER = AtomicLongFieldUpdater.newUpdater(Cell.class, "value");

        Cell(long x) {
            VALUE_UPDATER.set(this, x);
        }

        final boolean cas(long cmp, long val) {
            return VALUE_UPDATER.compareAndSet(this, cmp, val);
        }
    }
}

