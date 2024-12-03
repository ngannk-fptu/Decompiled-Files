/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import org.HdrHistogram.HistogramIterationValue;

public class DoubleHistogramIterationValue {
    private final HistogramIterationValue integerHistogramIterationValue;

    void reset() {
        this.integerHistogramIterationValue.reset();
    }

    DoubleHistogramIterationValue(HistogramIterationValue integerHistogramIterationValue) {
        this.integerHistogramIterationValue = integerHistogramIterationValue;
    }

    public String toString() {
        return "valueIteratedTo:" + this.getValueIteratedTo() + ", prevValueIteratedTo:" + this.getValueIteratedFrom() + ", countAtValueIteratedTo:" + this.getCountAtValueIteratedTo() + ", countAddedInThisIterationStep:" + this.getCountAddedInThisIterationStep() + ", totalCountToThisValue:" + this.getTotalCountToThisValue() + ", totalValueToThisValue:" + this.getTotalValueToThisValue() + ", percentile:" + this.getPercentile() + ", percentileLevelIteratedTo:" + this.getPercentileLevelIteratedTo();
    }

    public double getValueIteratedTo() {
        return (double)this.integerHistogramIterationValue.getValueIteratedTo() * this.integerHistogramIterationValue.getIntegerToDoubleValueConversionRatio();
    }

    public double getValueIteratedFrom() {
        return (double)this.integerHistogramIterationValue.getValueIteratedFrom() * this.integerHistogramIterationValue.getIntegerToDoubleValueConversionRatio();
    }

    public long getCountAtValueIteratedTo() {
        return this.integerHistogramIterationValue.getCountAtValueIteratedTo();
    }

    public long getCountAddedInThisIterationStep() {
        return this.integerHistogramIterationValue.getCountAddedInThisIterationStep();
    }

    public long getTotalCountToThisValue() {
        return this.integerHistogramIterationValue.getTotalCountToThisValue();
    }

    public double getTotalValueToThisValue() {
        return (double)this.integerHistogramIterationValue.getTotalValueToThisValue() * this.integerHistogramIterationValue.getIntegerToDoubleValueConversionRatio();
    }

    public double getPercentile() {
        return this.integerHistogramIterationValue.getPercentile();
    }

    public double getPercentileLevelIteratedTo() {
        return this.integerHistogramIterationValue.getPercentileLevelIteratedTo();
    }

    public HistogramIterationValue getIntegerHistogramIterationValue() {
        return this.integerHistogramIterationValue;
    }
}

