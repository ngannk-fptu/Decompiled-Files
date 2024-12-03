/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util;

import java.util.Objects;

public class BoundedCount {
    private final long count;
    private final boolean exact;

    public static BoundedCount exactly(long count) {
        return new BoundedCount(count, true);
    }

    public static BoundedCount atLeast(long count) {
        return new BoundedCount(count, false);
    }

    public static BoundedCount fromCountedItemsAndLimit(long count, long potentialMaxCount) {
        return count < potentialMaxCount ? BoundedCount.exactly(count) : BoundedCount.atLeast(potentialMaxCount);
    }

    private BoundedCount(long count, boolean exactly) {
        if (count < 0L) {
            throw new IllegalArgumentException("Bounded count can not be instantiated with negative count: " + count);
        }
        this.count = count;
        this.exact = exactly;
    }

    public long getCount() {
        return this.count;
    }

    public boolean isExact() {
        return this.exact;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoundedCount)) {
            return false;
        }
        BoundedCount that = (BoundedCount)o;
        return this.count == that.count && this.exact == that.exact;
    }

    public int hashCode() {
        return Objects.hash(this.getCount(), this.isExact());
    }

    public String toString() {
        return (this.exact ? "exactly" : "atLeast") + "(" + this.count + ")";
    }
}

