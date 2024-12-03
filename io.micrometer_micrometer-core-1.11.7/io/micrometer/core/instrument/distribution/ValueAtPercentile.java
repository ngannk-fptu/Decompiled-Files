/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.util.TimeUtils;
import java.util.concurrent.TimeUnit;

public final class ValueAtPercentile {
    private final double percentile;
    private final double value;

    public ValueAtPercentile(double percentile, double value) {
        this.percentile = percentile;
        this.value = value;
    }

    public double percentile() {
        return this.percentile;
    }

    public double value() {
        return this.value;
    }

    public double value(TimeUnit unit) {
        return TimeUtils.nanosToUnit(this.value, unit);
    }

    public String toString() {
        return "(" + this.value + " at " + this.percentile * 100.0 + "%)";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ValueAtPercentile that = (ValueAtPercentile)o;
        return Double.compare(that.percentile, this.percentile) == 0 && Double.compare(that.value, this.value) == 0;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.percentile);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.value);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
}

