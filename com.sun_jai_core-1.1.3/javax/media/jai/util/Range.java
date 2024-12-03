/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.util;

import java.io.Serializable;
import javax.media.jai.util.JaiI18N;

public class Range
implements Serializable {
    private Class elementClass;
    private Comparable minValue;
    private Comparable maxValue;
    private boolean isMinIncluded = true;
    private boolean isMaxIncluded = true;

    public Range(Class elementClass, Comparable minValue, Comparable maxValue) {
        if (minValue == null && maxValue == null) {
            Class<?> c = null;
            try {
                c = Class.forName("java.lang.Comparable");
            }
            catch (ClassNotFoundException e) {
                // empty catch block
            }
            if (!c.isAssignableFrom(elementClass)) {
                throw new IllegalArgumentException(JaiI18N.getString("Range0"));
            }
        }
        this.elementClass = elementClass;
        if (minValue != null && minValue.getClass() != this.elementClass) {
            throw new IllegalArgumentException(JaiI18N.getString("Range1"));
        }
        this.minValue = minValue;
        if (maxValue != null && maxValue.getClass() != this.elementClass) {
            throw new IllegalArgumentException(JaiI18N.getString("Range2"));
        }
        this.maxValue = maxValue;
    }

    public Range(Class elementClass, Comparable minValue, boolean isMinIncluded, Comparable maxValue, boolean isMaxIncluded) {
        this(elementClass, minValue, maxValue);
        this.isMinIncluded = isMinIncluded;
        this.isMaxIncluded = isMaxIncluded;
    }

    public boolean isMinIncluded() {
        if (this.minValue == null) {
            return true;
        }
        return this.isMinIncluded;
    }

    public boolean isMaxIncluded() {
        if (this.maxValue == null) {
            return true;
        }
        return this.isMaxIncluded;
    }

    public Class getElementClass() {
        return this.elementClass;
    }

    public Comparable getMinValue() {
        return this.minValue;
    }

    public Comparable getMaxValue() {
        return this.maxValue;
    }

    public boolean contains(Comparable value) {
        if (value != null && value.getClass() != this.elementClass) {
            throw new IllegalArgumentException(JaiI18N.getString("Range3"));
        }
        if (this.isEmpty()) {
            return false;
        }
        return this.isUnderUpperBound(value) && this.isOverLowerBound(value);
    }

    private boolean isUnderUpperBound(Comparable value) {
        if (this.maxValue == null) {
            return true;
        }
        if (value == null) {
            return false;
        }
        if (this.isMaxIncluded) {
            return this.maxValue.compareTo(value) >= 0;
        }
        return this.maxValue.compareTo(value) > 0;
    }

    private boolean isOverLowerBound(Comparable value) {
        if (this.minValue == null) {
            return true;
        }
        if (value == null) {
            return false;
        }
        if (this.isMinIncluded) {
            return this.minValue.compareTo(value) <= 0;
        }
        return this.minValue.compareTo(value) < 0;
    }

    public boolean contains(Range range) {
        boolean maxSide;
        if (range == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Range5"));
        }
        if (this.elementClass != range.getElementClass()) {
            throw new IllegalArgumentException(JaiI18N.getString("Range4"));
        }
        if (range.isEmpty()) {
            return true;
        }
        Comparable min = range.getMinValue();
        Comparable max = range.getMaxValue();
        if (max == null) {
            maxSide = this.maxValue == null;
        } else {
            boolean bl = maxSide = this.isUnderUpperBound(max) || this.isMaxIncluded == range.isMaxIncluded() && max.equals(this.maxValue);
        }
        boolean minSide = min == null ? this.minValue == null : this.isOverLowerBound(min) || this.isMinIncluded == range.isMinIncluded() && min.equals(this.minValue);
        return minSide && maxSide;
    }

    public boolean intersects(Range range) {
        if (range == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Range5"));
        }
        if (this.elementClass != range.getElementClass()) {
            throw new IllegalArgumentException(JaiI18N.getString("Range4"));
        }
        return !this.intersect(range).isEmpty();
    }

    public Range union(Range range) {
        if (range == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Range5"));
        }
        if (this.elementClass != range.getElementClass()) {
            throw new IllegalArgumentException(JaiI18N.getString("Range4"));
        }
        if (this.isEmpty()) {
            return new Range(this.elementClass, range.getMinValue(), range.isMinIncluded(), range.getMaxValue(), range.isMaxIncluded());
        }
        if (range.isEmpty()) {
            return new Range(this.elementClass, this.minValue, this.isMinIncluded, this.maxValue, this.isMaxIncluded);
        }
        boolean containMin = !this.isOverLowerBound(range.getMinValue());
        boolean containMax = !this.isUnderUpperBound(range.getMaxValue());
        Comparable minValue = containMin ? range.getMinValue() : this.minValue;
        Comparable maxValue = containMax ? range.getMaxValue() : this.maxValue;
        boolean isMinIncluded = containMin ? range.isMinIncluded() : this.isMinIncluded;
        boolean isMaxIncluded = containMax ? range.isMaxIncluded() : this.isMaxIncluded;
        return new Range(this.elementClass, minValue, isMinIncluded, maxValue, isMaxIncluded);
    }

    public Range intersect(Range range) {
        if (range == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Range5"));
        }
        if (this.elementClass != range.getElementClass()) {
            throw new IllegalArgumentException(JaiI18N.getString("Range4"));
        }
        if (this.isEmpty()) {
            Comparable temp = this.minValue;
            if (temp == null) {
                temp = this.maxValue;
            }
            return new Range(this.elementClass, temp, false, temp, false);
        }
        if (range.isEmpty()) {
            Comparable temp = range.getMinValue();
            if (temp == null) {
                temp = range.getMaxValue();
            }
            return new Range(this.elementClass, temp, false, temp, false);
        }
        boolean containMin = !this.isOverLowerBound(range.getMinValue());
        boolean containMax = !this.isUnderUpperBound(range.getMaxValue());
        Comparable minValue = containMin ? this.minValue : range.getMinValue();
        Comparable maxValue = containMax ? this.maxValue : range.getMaxValue();
        boolean isMinIncluded = containMin ? this.isMinIncluded : range.isMinIncluded();
        boolean isMaxIncluded = containMax ? this.isMaxIncluded : range.isMaxIncluded();
        return new Range(this.elementClass, minValue, isMinIncluded, maxValue, isMaxIncluded);
    }

    public Range[] subtract(Range range) {
        if (range == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Range5"));
        }
        if (this.elementClass != range.getElementClass()) {
            throw new IllegalArgumentException(JaiI18N.getString("Range4"));
        }
        if (this.isEmpty() || range.isEmpty()) {
            Range[] ra = new Range[]{new Range(this.elementClass, this.minValue, this.isMinIncluded, this.maxValue, this.isMaxIncluded)};
            return ra;
        }
        Comparable min = range.getMinValue();
        Comparable max = range.getMaxValue();
        boolean minIn = range.isMinIncluded();
        boolean maxIn = range.isMaxIncluded();
        if (this.minValue == null && this.maxValue == null && min == null && max == null) {
            Range[] ra = new Range[]{null};
            return ra;
        }
        boolean containMin = this.contains(min);
        boolean containMax = this.contains(max);
        if (containMin && containMax) {
            Range r1 = new Range(this.elementClass, this.minValue, this.isMinIncluded, min, !minIn);
            Range r2 = new Range(this.elementClass, max, !maxIn, this.maxValue, this.isMaxIncluded);
            if (r1.isEmpty() || this.minValue == null && min == null) {
                Range[] ra = new Range[]{r2};
                return ra;
            }
            if (r2.isEmpty() || this.maxValue == null && max == null) {
                Range[] ra = new Range[]{r1};
                return ra;
            }
            Range[] ra = new Range[]{r1, r2};
            return ra;
        }
        if (containMax) {
            Range[] ra = new Range[]{new Range(this.elementClass, max, !maxIn, this.maxValue, this.isMaxIncluded)};
            return ra;
        }
        if (containMin) {
            Range[] ra = new Range[]{new Range(this.elementClass, this.minValue, this.isMinIncluded, min, !minIn)};
            return ra;
        }
        if (min != null && !this.isUnderUpperBound(min) || max != null && !this.isOverLowerBound(max)) {
            Range[] ra = new Range[]{new Range(this.elementClass, this.minValue, this.isMinIncluded, this.maxValue, this.isMaxIncluded)};
            return ra;
        }
        min = this.minValue == null ? this.maxValue : this.minValue;
        Range[] ra = new Range[]{new Range(this.elementClass, min, false, min, false)};
        return ra;
    }

    public int hashCode() {
        int code = this.elementClass.hashCode();
        if (this.isEmpty()) {
            return code;
        }
        code ^= Integer.MAX_VALUE;
        if (this.minValue != null) {
            code ^= this.minValue.hashCode();
            if (this.isMinIncluded) {
                code ^= 0xFFFF0000;
            }
        }
        if (this.maxValue != null) {
            code ^= this.maxValue.hashCode() * 31;
            if (this.isMaxIncluded) {
                code ^= 0xFFFF;
            }
        }
        return code;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Range)) {
            return false;
        }
        Range r = (Range)other;
        if (this.elementClass != r.getElementClass()) {
            return false;
        }
        if (this.isEmpty() && r.isEmpty()) {
            return true;
        }
        Comparable min = r.getMinValue();
        if (this.minValue != null) {
            if (!this.minValue.equals(min)) {
                return false;
            }
            if (this.isMinIncluded != r.isMinIncluded()) {
                return false;
            }
        } else if (min != null) {
            return false;
        }
        Comparable max = r.getMaxValue();
        if (this.maxValue != null) {
            if (!this.maxValue.equals(max)) {
                return false;
            }
            if (this.isMaxIncluded != r.isMaxIncluded()) {
                return false;
            }
        } else if (max != null) {
            return false;
        }
        return true;
    }

    public boolean isEmpty() {
        if (this.minValue == null || this.maxValue == null) {
            return false;
        }
        int cmp = this.minValue.compareTo(this.maxValue);
        if (cmp > 0) {
            return true;
        }
        if (cmp == 0) {
            return !(this.isMinIncluded & this.isMaxIncluded);
        }
        return false;
    }

    public String toString() {
        char c2;
        char c1 = this.isMinIncluded ? (char)'[' : '(';
        char c = c2 = this.isMaxIncluded ? (char)']' : ')';
        if (this.minValue != null && this.maxValue != null) {
            return new String(c1 + this.minValue.toString() + ", " + this.maxValue.toString() + c2);
        }
        if (this.maxValue != null) {
            return new String(c1 + "---, " + this.maxValue.toString() + c2);
        }
        if (this.minValue != null) {
            return new String(c1 + this.minValue.toString() + ", " + "---" + c2);
        }
        return new String(c1 + "---, ---" + c2);
    }
}

