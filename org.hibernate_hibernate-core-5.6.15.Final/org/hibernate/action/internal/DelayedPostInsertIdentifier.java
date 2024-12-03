/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class DelayedPostInsertIdentifier
implements Serializable,
Comparable<DelayedPostInsertIdentifier> {
    private static final AtomicLong SEQUENCE = new AtomicLong(0L);
    private final long identifier;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DelayedPostInsertIdentifier() {
        long value = SEQUENCE.incrementAndGet();
        if (value < 0L) {
            AtomicLong atomicLong = SEQUENCE;
            synchronized (atomicLong) {
                value = SEQUENCE.incrementAndGet();
                if (value < 0L) {
                    SEQUENCE.set(0L);
                    value = 0L;
                }
            }
        }
        this.identifier = value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DelayedPostInsertIdentifier that = (DelayedPostInsertIdentifier)o;
        return this.identifier == that.identifier;
    }

    public int hashCode() {
        return (int)(this.identifier ^ this.identifier >>> 32);
    }

    public String toString() {
        return "<delayed:" + this.identifier + ">";
    }

    @Override
    public int compareTo(DelayedPostInsertIdentifier that) {
        if (this.identifier < that.identifier) {
            return -1;
        }
        if (this.identifier > that.identifier) {
            return 1;
        }
        return 0;
    }
}

