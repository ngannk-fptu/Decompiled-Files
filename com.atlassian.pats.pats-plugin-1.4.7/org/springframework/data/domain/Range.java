/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.domain;

import java.util.Optional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public final class Range<T extends Comparable<T>> {
    private static final Range<?> UNBOUNDED = Range.of(Bound.unbounded(), Bound.access$000());
    private final Bound<T> lowerBound;
    private final Bound<T> upperBound;

    private Range(Bound<T> lowerBound, Bound<T> upperBound) {
        Assert.notNull(lowerBound, (String)"Lower bound must not be null!");
        Assert.notNull(upperBound, (String)"Upper bound must not be null!");
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public static <T extends Comparable<T>> Range<T> unbounded() {
        return UNBOUNDED;
    }

    public static <T extends Comparable<T>> Range<T> closed(T from, T to) {
        return new Range<T>(Bound.inclusive(from), Bound.inclusive(to));
    }

    public static <T extends Comparable<T>> Range<T> open(T from, T to) {
        return new Range<T>(Bound.exclusive(from), Bound.exclusive(to));
    }

    public static <T extends Comparable<T>> Range<T> leftOpen(T from, T to) {
        return new Range<T>(Bound.exclusive(from), Bound.inclusive(to));
    }

    public static <T extends Comparable<T>> Range<T> rightOpen(T from, T to) {
        return new Range<T>(Bound.inclusive(from), Bound.exclusive(to));
    }

    public static <T extends Comparable<T>> Range<T> leftUnbounded(Bound<T> to) {
        return new Range(Bound.unbounded(), to);
    }

    public static <T extends Comparable<T>> Range<T> rightUnbounded(Bound<T> from) {
        return new Range<T>(from, Bound.unbounded());
    }

    public static <T extends Comparable<T>> RangeBuilder<T> from(Bound<T> lower) {
        Assert.notNull(lower, (String)"Lower bound must not be null!");
        return new RangeBuilder<T>(lower);
    }

    public static <T extends Comparable<T>> Range<T> of(Bound<T> lowerBound, Bound<T> upperBound) {
        return new Range<T>(lowerBound, upperBound);
    }

    public static <T extends Comparable<T>> Range<T> just(T value) {
        return Range.closed(value, value);
    }

    public boolean contains(T value) {
        Assert.notNull(value, (String)"Reference value must not be null!");
        boolean greaterThanLowerBound = this.lowerBound.getValue().map(it -> this.lowerBound.isInclusive() ? it.compareTo(value) <= 0 : it.compareTo(value) < 0).orElse(true);
        boolean lessThanUpperBound = this.upperBound.getValue().map(it -> this.upperBound.isInclusive() ? it.compareTo(value) >= 0 : it.compareTo(value) > 0).orElse(true);
        return greaterThanLowerBound && lessThanUpperBound;
    }

    public String toString() {
        return String.format("%s-%s", this.lowerBound.toPrefixString(), this.upperBound.toSuffixString());
    }

    public Bound<T> getLowerBound() {
        return this.lowerBound;
    }

    public Bound<T> getUpperBound() {
        return this.upperBound;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Range)) {
            return false;
        }
        Range range = (Range)o;
        if (!ObjectUtils.nullSafeEquals(this.lowerBound, range.lowerBound)) {
            return false;
        }
        return ObjectUtils.nullSafeEquals(this.upperBound, range.upperBound);
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.lowerBound);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.upperBound);
        return result;
    }

    public static class RangeBuilder<T extends Comparable<T>> {
        private final Bound<T> lower;

        RangeBuilder(Bound<T> lower) {
            this.lower = lower;
        }

        public Range<T> to(Bound<T> upper) {
            Assert.notNull(upper, (String)"Upper bound must not be null!");
            return new Range(this.lower, upper);
        }
    }

    public static final class Bound<T extends Comparable<T>> {
        private static final Bound<?> UNBOUNDED = new Bound(Optional.empty(), true);
        private final Optional<T> value;
        private final boolean inclusive;

        private Bound(Optional<T> value, boolean inclusive) {
            this.value = value;
            this.inclusive = inclusive;
        }

        public static <T extends Comparable<T>> Bound<T> unbounded() {
            return UNBOUNDED;
        }

        public boolean isBounded() {
            return this.value.isPresent();
        }

        public static <T extends Comparable<T>> Bound<T> inclusive(T value) {
            Assert.notNull(value, (String)"Value must not be null!");
            return new Bound<T>(Optional.of(value), true);
        }

        public static Bound<Integer> inclusive(int value) {
            return Bound.inclusive(Integer.valueOf(value));
        }

        public static Bound<Long> inclusive(long value) {
            return Bound.inclusive(Long.valueOf(value));
        }

        public static Bound<Float> inclusive(float value) {
            return Bound.inclusive(Float.valueOf(value));
        }

        public static Bound<Double> inclusive(double value) {
            return Bound.inclusive(Double.valueOf(value));
        }

        public static <T extends Comparable<T>> Bound<T> exclusive(T value) {
            Assert.notNull(value, (String)"Value must not be null!");
            return new Bound<T>(Optional.of(value), false);
        }

        public static Bound<Integer> exclusive(int value) {
            return Bound.exclusive(Integer.valueOf(value));
        }

        public static Bound<Long> exclusive(long value) {
            return Bound.exclusive(Long.valueOf(value));
        }

        public static Bound<Float> exclusive(float value) {
            return Bound.exclusive(Float.valueOf(value));
        }

        public static Bound<Double> exclusive(double value) {
            return Bound.exclusive(Double.valueOf(value));
        }

        String toPrefixString() {
            return this.getValue().map(Object::toString).map(it -> this.isInclusive() ? "[".concat((String)it) : "(".concat((String)it)).orElse("unbounded");
        }

        String toSuffixString() {
            return this.getValue().map(Object::toString).map(it -> this.isInclusive() ? it.concat("]") : it.concat(")")).orElse("unbounded");
        }

        public String toString() {
            return this.value.map(Object::toString).orElse("unbounded");
        }

        public Optional<T> getValue() {
            return this.value;
        }

        public boolean isInclusive() {
            return this.inclusive;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Bound)) {
                return false;
            }
            Bound bound = (Bound)o;
            if (this.inclusive != bound.inclusive) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.value, bound.value);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode(this.value);
            result = 31 * result + (this.inclusive ? 1 : 0);
            return result;
        }

        static /* synthetic */ Bound access$000() {
            return UNBOUNDED;
        }
    }
}

