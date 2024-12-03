/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeType;
import net.sf.ehcache.search.expression.ComparableValue;

public class Between
extends ComparableValue {
    private final Comparable min;
    private final Comparable max;
    private final boolean minInclusive;
    private final boolean maxInclusive;

    public Between(String attributeName, Object min, Object max, boolean minInclusive, boolean maxInclusive) {
        super(attributeName, Between.computeType(attributeName, min, max));
        this.min = (Comparable)min;
        this.max = (Comparable)max;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    private static AttributeType computeType(String attributeName, Object min, Object max) {
        AttributeType maxType;
        if (min == null || max == null) {
            throw new NullPointerException();
        }
        AttributeType minType = AttributeType.typeFor(attributeName, min);
        if (minType != (maxType = AttributeType.typeFor(attributeName, max))) {
            throw new SearchException("Different types for min (" + minType + ") and max (" + maxType + ")");
        }
        return minType;
    }

    public Comparable getMin() {
        return this.min;
    }

    public Comparable getMax() {
        return this.max;
    }

    public boolean isMinInclusive() {
        return this.minInclusive;
    }

    public boolean isMaxInclusive() {
        return this.maxInclusive;
    }

    @Override
    protected boolean executeComparable(Comparable attributeValue) {
        int minCmp = attributeValue.compareTo(this.min);
        if (minCmp < 0 || minCmp == 0 && !this.minInclusive) {
            return false;
        }
        int maxCmp = attributeValue.compareTo(this.max);
        return maxCmp <= 0 && (maxCmp != 0 || this.maxInclusive);
    }

    @Override
    protected boolean executeComparableString(Comparable attributeValue) {
        int minCmp = Between.luceneStringCompare(attributeValue.toString(), this.min.toString());
        if (minCmp < 0 || minCmp == 0 && !this.minInclusive) {
            return false;
        }
        int maxCmp = Between.luceneStringCompare(attributeValue.toString(), this.max.toString());
        return maxCmp <= 0 && (maxCmp != 0 || this.maxInclusive);
    }
}

