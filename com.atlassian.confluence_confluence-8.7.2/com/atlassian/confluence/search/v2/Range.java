/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.search.v2;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Range<T> {
    private final T from;
    private final T to;
    private final boolean includeFrom;
    private final boolean includeTo;

    public Range(T from, T to, boolean includeFrom, boolean includeTo) {
        this.from = from;
        this.to = to;
        this.includeFrom = includeFrom;
        this.includeTo = includeTo;
    }

    public static <R> Range<R> range(R from, R to, boolean includeFrom, boolean includeTo) {
        return new Range<R>(from, to, includeFrom, includeTo);
    }

    public T getFrom() {
        return this.from;
    }

    public T getTo() {
        return this.to;
    }

    public boolean isIncludeFrom() {
        return this.includeFrom;
    }

    public boolean isIncludeTo() {
        return this.includeTo;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Range other = (Range)obj;
        return new EqualsBuilder().append(this.from, other.from).append(this.to, other.to).append(this.includeFrom, other.includeFrom).append(this.includeTo, other.includeTo).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(113, 37).append(this.from).append(this.to).append(this.includeFrom).append(this.includeTo).toHashCode();
    }

    public static class Builder<T> {
        private Builder() {
        }

        public static <T> Builder<T> range(Class<T> clazz) {
            return new Builder<T>();
        }

        public Range<T> greaterThan(T value) {
            return new Range<Object>(value, null, false, true);
        }

        public Range<T> greaterThanEquals(T value) {
            return new Range<Object>(value, null, true, true);
        }

        public Range<T> lessThan(T value) {
            return new Range<Object>(null, value, true, false);
        }

        public Range<T> lessThanEquals(T value) {
            return new Range<Object>(null, value, true, true);
        }

        public Range<T> equalsOp(T value) {
            return new Range<T>(value, value, true, true);
        }

        public Range<T> equalsOp(T lowerBound, T upperBound) {
            return new Range<T>(lowerBound, upperBound, true, false);
        }
    }
}

