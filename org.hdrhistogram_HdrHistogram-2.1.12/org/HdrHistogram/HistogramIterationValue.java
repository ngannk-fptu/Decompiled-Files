/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

public class HistogramIterationValue {
    private long valueIteratedTo;
    private long valueIteratedFrom;
    private long countAtValueIteratedTo;
    private long countAddedInThisIterationStep;
    private long totalCountToThisValue;
    private long totalValueToThisValue;
    private double percentile;
    private double percentileLevelIteratedTo;
    private double integerToDoubleValueConversionRatio;

    void set(long valueIteratedTo, long valueIteratedFrom, long countAtValueIteratedTo, long countInThisIterationStep, long totalCountToThisValue, long totalValueToThisValue, double percentile, double percentileLevelIteratedTo, double integerToDoubleValueConversionRatio) {
        this.valueIteratedTo = valueIteratedTo;
        this.valueIteratedFrom = valueIteratedFrom;
        this.countAtValueIteratedTo = countAtValueIteratedTo;
        this.countAddedInThisIterationStep = countInThisIterationStep;
        this.totalCountToThisValue = totalCountToThisValue;
        this.totalValueToThisValue = totalValueToThisValue;
        this.percentile = percentile;
        this.percentileLevelIteratedTo = percentileLevelIteratedTo;
        this.integerToDoubleValueConversionRatio = integerToDoubleValueConversionRatio;
    }

    void reset() {
        this.valueIteratedTo = 0L;
        this.valueIteratedFrom = 0L;
        this.countAtValueIteratedTo = 0L;
        this.countAddedInThisIterationStep = 0L;
        this.totalCountToThisValue = 0L;
        this.totalValueToThisValue = 0L;
        this.percentile = 0.0;
        this.percentileLevelIteratedTo = 0.0;
    }

    HistogramIterationValue() {
    }

    public String toString() {
        return "valueIteratedTo:" + this.valueIteratedTo + ", prevValueIteratedTo:" + this.valueIteratedFrom + ", countAtValueIteratedTo:" + this.countAtValueIteratedTo + ", countAddedInThisIterationStep:" + this.countAddedInThisIterationStep + ", totalCountToThisValue:" + this.totalCountToThisValue + ", totalValueToThisValue:" + this.totalValueToThisValue + ", percentile:" + this.percentile + ", percentileLevelIteratedTo:" + this.percentileLevelIteratedTo;
    }

    public long getValueIteratedTo() {
        return this.valueIteratedTo;
    }

    public double getDoubleValueIteratedTo() {
        return (double)this.valueIteratedTo * this.integerToDoubleValueConversionRatio;
    }

    public long getValueIteratedFrom() {
        return this.valueIteratedFrom;
    }

    public double getDoubleValueIteratedFrom() {
        return (double)this.valueIteratedFrom * this.integerToDoubleValueConversionRatio;
    }

    public long getCountAtValueIteratedTo() {
        return this.countAtValueIteratedTo;
    }

    public long getCountAddedInThisIterationStep() {
        return this.countAddedInThisIterationStep;
    }

    public long getTotalCountToThisValue() {
        return this.totalCountToThisValue;
    }

    public long getTotalValueToThisValue() {
        return this.totalValueToThisValue;
    }

    public double getPercentile() {
        return this.percentile;
    }

    public double getPercentileLevelIteratedTo() {
        return this.percentileLevelIteratedTo;
    }

    public double getIntegerToDoubleValueConversionRatio() {
        return this.integerToDoubleValueConversionRatio;
    }
}

