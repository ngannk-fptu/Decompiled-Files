/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.msgs.restricted;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Random;

final class CompactInterner {
    @VisibleForTesting
    static final int INITIAL_SIZE = 1024;
    private static final int MAX_EXPECTED_COLLISION_COUNT = 4;
    private static final int GROWTH_DENOMINATOR = 4;
    private Object[] table = new Object[1024];
    private int count = 0;
    private long collisions = 0L;

    public synchronized <T> T intern(T value) {
        Preconditions.checkNotNull(value);
        Random generator = new Random(value.hashCode());
        int tries = 0;
        while (true) {
            int index;
            Object candidate;
            if ((candidate = this.table[index = generator.nextInt(this.table.length)]) == null) {
                ++this.count;
                this.collisions += (long)tries;
                this.table[index] = value;
                this.rehashIfNeeded();
                return value;
            }
            if (candidate.equals(value)) {
                Preconditions.checkArgument((value.getClass() == candidate.getClass() ? 1 : 0) != 0, (Object)("Interned objects are equals() but different classes: " + value + " and " + candidate));
                return (T)candidate;
            }
            ++tries;
        }
    }

    private void rehashIfNeeded() {
        int currentSize = this.table.length;
        if (currentSize - this.count >= currentSize / 5) {
            return;
        }
        Object[] oldTable = this.table;
        int newSize = currentSize + currentSize / 4;
        this.table = new Object[newSize];
        this.count = 0;
        for (Object element : oldTable) {
            if (element == null) continue;
            this.intern(element);
        }
    }

    @VisibleForTesting
    double getAverageCollisions() {
        return 1.0 * (double)this.collisions / (double)this.count;
    }

    @VisibleForTesting
    static double getAverageCollisionsBound() {
        double x = Math.max(4, 4);
        return x * Math.log(x + 1.0) + 1.0;
    }

    @VisibleForTesting
    double getOverhead() {
        return 1.0 * (double)(this.table.length - this.count) / (double)this.count;
    }

    @VisibleForTesting
    static final double getWorstCaseOverhead() {
        return 0.5625;
    }
}

