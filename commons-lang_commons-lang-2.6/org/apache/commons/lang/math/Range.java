/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.math;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.text.StrBuilder;

public abstract class Range {
    public abstract Number getMinimumNumber();

    public long getMinimumLong() {
        return this.getMinimumNumber().longValue();
    }

    public int getMinimumInteger() {
        return this.getMinimumNumber().intValue();
    }

    public double getMinimumDouble() {
        return this.getMinimumNumber().doubleValue();
    }

    public float getMinimumFloat() {
        return this.getMinimumNumber().floatValue();
    }

    public abstract Number getMaximumNumber();

    public long getMaximumLong() {
        return this.getMaximumNumber().longValue();
    }

    public int getMaximumInteger() {
        return this.getMaximumNumber().intValue();
    }

    public double getMaximumDouble() {
        return this.getMaximumNumber().doubleValue();
    }

    public float getMaximumFloat() {
        return this.getMaximumNumber().floatValue();
    }

    public abstract boolean containsNumber(Number var1);

    public boolean containsLong(Number value) {
        if (value == null) {
            return false;
        }
        return this.containsLong(value.longValue());
    }

    public boolean containsLong(long value) {
        return value >= this.getMinimumLong() && value <= this.getMaximumLong();
    }

    public boolean containsInteger(Number value) {
        if (value == null) {
            return false;
        }
        return this.containsInteger(value.intValue());
    }

    public boolean containsInteger(int value) {
        return value >= this.getMinimumInteger() && value <= this.getMaximumInteger();
    }

    public boolean containsDouble(Number value) {
        if (value == null) {
            return false;
        }
        return this.containsDouble(value.doubleValue());
    }

    public boolean containsDouble(double value) {
        int compareMin = NumberUtils.compare(this.getMinimumDouble(), value);
        int compareMax = NumberUtils.compare(this.getMaximumDouble(), value);
        return compareMin <= 0 && compareMax >= 0;
    }

    public boolean containsFloat(Number value) {
        if (value == null) {
            return false;
        }
        return this.containsFloat(value.floatValue());
    }

    public boolean containsFloat(float value) {
        int compareMin = NumberUtils.compare(this.getMinimumFloat(), value);
        int compareMax = NumberUtils.compare(this.getMaximumFloat(), value);
        return compareMin <= 0 && compareMax >= 0;
    }

    public boolean containsRange(Range range) {
        if (range == null) {
            return false;
        }
        return this.containsNumber(range.getMinimumNumber()) && this.containsNumber(range.getMaximumNumber());
    }

    public boolean overlapsRange(Range range) {
        if (range == null) {
            return false;
        }
        return range.containsNumber(this.getMinimumNumber()) || range.containsNumber(this.getMaximumNumber()) || this.containsNumber(range.getMinimumNumber());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Range range = (Range)obj;
        return this.getMinimumNumber().equals(range.getMinimumNumber()) && this.getMaximumNumber().equals(range.getMaximumNumber());
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.getClass().hashCode();
        result = 37 * result + this.getMinimumNumber().hashCode();
        result = 37 * result + this.getMaximumNumber().hashCode();
        return result;
    }

    public String toString() {
        StrBuilder buf = new StrBuilder(32);
        buf.append("Range[");
        buf.append(this.getMinimumNumber());
        buf.append(',');
        buf.append(this.getMaximumNumber());
        buf.append(']');
        return buf.toString();
    }
}

