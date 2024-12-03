/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.search.MultiTermQueryWrapperFilter;
import org.apache.lucene.search.NumericRangeQuery;

public final class NumericRangeFilter<T extends Number>
extends MultiTermQueryWrapperFilter<NumericRangeQuery<T>> {
    private NumericRangeFilter(NumericRangeQuery<T> query) {
        super(query);
    }

    public static NumericRangeFilter<Long> newLongRange(String field, int precisionStep, Long min, Long max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeFilter<Long>(NumericRangeQuery.newLongRange(field, precisionStep, min, max, minInclusive, maxInclusive));
    }

    public static NumericRangeFilter<Long> newLongRange(String field, Long min, Long max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeFilter<Long>(NumericRangeQuery.newLongRange(field, min, max, minInclusive, maxInclusive));
    }

    public static NumericRangeFilter<Integer> newIntRange(String field, int precisionStep, Integer min, Integer max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeFilter<Integer>(NumericRangeQuery.newIntRange(field, precisionStep, min, max, minInclusive, maxInclusive));
    }

    public static NumericRangeFilter<Integer> newIntRange(String field, Integer min, Integer max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeFilter<Integer>(NumericRangeQuery.newIntRange(field, min, max, minInclusive, maxInclusive));
    }

    public static NumericRangeFilter<Double> newDoubleRange(String field, int precisionStep, Double min, Double max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeFilter<Double>(NumericRangeQuery.newDoubleRange(field, precisionStep, min, max, minInclusive, maxInclusive));
    }

    public static NumericRangeFilter<Double> newDoubleRange(String field, Double min, Double max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeFilter<Double>(NumericRangeQuery.newDoubleRange(field, min, max, minInclusive, maxInclusive));
    }

    public static NumericRangeFilter<Float> newFloatRange(String field, int precisionStep, Float min, Float max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeFilter<Float>(NumericRangeQuery.newFloatRange(field, precisionStep, min, max, minInclusive, maxInclusive));
    }

    public static NumericRangeFilter<Float> newFloatRange(String field, Float min, Float max, boolean minInclusive, boolean maxInclusive) {
        return new NumericRangeFilter<Float>(NumericRangeQuery.newFloatRange(field, min, max, minInclusive, maxInclusive));
    }

    public boolean includesMin() {
        return ((NumericRangeQuery)this.query).includesMin();
    }

    public boolean includesMax() {
        return ((NumericRangeQuery)this.query).includesMax();
    }

    public T getMin() {
        return ((NumericRangeQuery)this.query).getMin();
    }

    public T getMax() {
        return ((NumericRangeQuery)this.query).getMax();
    }

    public int getPrecisionStep() {
        return ((NumericRangeQuery)this.query).getPrecisionStep();
    }
}

