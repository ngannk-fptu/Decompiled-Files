/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import net.sf.ehcache.search.expression.ComparableValue;

public class LessThan
extends ComparableValue {
    private final Comparable comparableValue;

    public LessThan(String attributeName, Object value) {
        super(attributeName, value);
        this.comparableValue = (Comparable)value;
    }

    @Override
    protected boolean executeComparable(Comparable attributeValue) {
        return attributeValue.compareTo(this.comparableValue) < 0;
    }

    @Override
    protected boolean executeComparableString(Comparable attributeValue) {
        return LessThan.luceneStringCompare(attributeValue.toString(), this.comparableValue.toString()) < 0;
    }

    public Comparable getComparableValue() {
        return this.comparableValue;
    }
}

