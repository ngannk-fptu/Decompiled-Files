/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.util.TimeUtils;
import java.util.concurrent.TimeUnit;

public final class CountAtBucket {
    private final double bucket;
    private final double count;

    @Deprecated
    public CountAtBucket(long bucket, double count) {
        this((double)bucket, count);
    }

    public CountAtBucket(double bucket, double count) {
        this.bucket = bucket;
        this.count = count;
    }

    public double bucket() {
        return this.bucket;
    }

    public double bucket(TimeUnit unit) {
        return TimeUtils.nanosToUnit(this.bucket, unit);
    }

    public double count() {
        return this.count;
    }

    boolean isPositiveInf() {
        return this.bucket == Double.POSITIVE_INFINITY || this.bucket == Double.MAX_VALUE || (long)this.bucket == Long.MAX_VALUE;
    }

    public String toString() {
        return "(" + this.count + " at " + this.bucket + ')';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CountAtBucket that = (CountAtBucket)o;
        return Double.compare(that.bucket, this.bucket) == 0 && Double.compare(that.count, this.count) == 0;
    }

    public int hashCode() {
        long tempBucket = Double.doubleToLongBits(this.bucket);
        int result = (int)(tempBucket ^ tempBucket >>> 32);
        long tempCount = Double.doubleToLongBits(this.count);
        result = 31 * result + (int)(tempCount ^ tempCount >>> 32);
        return result;
    }
}

