/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.jsr166e;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.terracotta.statistics.jsr166e.Striped64;

public class LongMaxUpdater
extends Striped64
implements Serializable {
    private static final long serialVersionUID = 7249069246863182397L;

    @Override
    final long fn(long v, long x) {
        return v > x ? v : x;
    }

    public LongMaxUpdater() {
        BASE_UPDATER.set(this, Long.MIN_VALUE);
    }

    public void update(long x) {
        long b;
        Striped64.Cell[] as = this.cells;
        if (this.cells != null || (b = this.base) < x && !this.casBase(b, x)) {
            long v;
            Striped64.Cell a;
            int n;
            boolean uncontended = true;
            Striped64.HashCode hc = (Striped64.HashCode)threadHashCode.get();
            int h = hc.code;
            if (as == null || (n = as.length) < 1 || (a = as[n - 1 & h]) == null || (v = a.value) < x && !(uncontended = a.cas(v, x))) {
                this.retryUpdate(x, hc, uncontended);
            }
        }
    }

    public long max() {
        Striped64.Cell[] as = this.cells;
        long max = this.base;
        if (as != null) {
            for (Striped64.Cell a : as) {
                long v;
                if (a == null || (v = a.value) <= max) continue;
                max = v;
            }
        }
        return max;
    }

    public void reset() {
        this.internalReset(Long.MIN_VALUE);
    }

    public long maxThenReset() {
        Striped64.Cell[] as = this.cells;
        long max = this.base;
        BASE_UPDATER.set(this, Long.MIN_VALUE);
        if (as != null) {
            for (Striped64.Cell a : as) {
                if (a == null) continue;
                long v = a.value;
                a.value = Long.MIN_VALUE;
                if (v <= max) continue;
                max = v;
            }
        }
        return max;
    }

    public String toString() {
        return Long.toString(this.max());
    }

    @Override
    public long longValue() {
        return this.max();
    }

    @Override
    public int intValue() {
        return (int)this.max();
    }

    @Override
    public float floatValue() {
        return this.max();
    }

    @Override
    public double doubleValue() {
        return this.max();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeLong(this.max());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        BUSY_UPDATER.set(this, 0);
        this.cells = null;
        BASE_UPDATER.set(this, s.readLong());
    }
}

