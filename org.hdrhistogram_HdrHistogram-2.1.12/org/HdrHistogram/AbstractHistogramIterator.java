/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.HistogramIterationValue;

abstract class AbstractHistogramIterator
implements Iterator<HistogramIterationValue> {
    AbstractHistogram histogram;
    long arrayTotalCount;
    int currentIndex;
    long currentValueAtIndex;
    long nextValueAtIndex;
    long prevValueIteratedTo;
    long totalCountToPrevIndex;
    long totalCountToCurrentIndex;
    long totalValueToCurrentIndex;
    long countAtThisValue;
    private boolean freshSubBucket;
    final HistogramIterationValue currentIterationValue = new HistogramIterationValue();
    private double integerToDoubleValueConversionRatio;

    AbstractHistogramIterator() {
    }

    void resetIterator(AbstractHistogram histogram) {
        this.histogram = histogram;
        this.arrayTotalCount = histogram.getTotalCount();
        this.integerToDoubleValueConversionRatio = histogram.getIntegerToDoubleValueConversionRatio();
        this.currentIndex = 0;
        this.currentValueAtIndex = 0L;
        this.nextValueAtIndex = 1 << histogram.unitMagnitude;
        this.prevValueIteratedTo = 0L;
        this.totalCountToPrevIndex = 0L;
        this.totalCountToCurrentIndex = 0L;
        this.totalValueToCurrentIndex = 0L;
        this.countAtThisValue = 0L;
        this.freshSubBucket = true;
        this.currentIterationValue.reset();
    }

    @Override
    public boolean hasNext() {
        if (this.histogram.getTotalCount() != this.arrayTotalCount) {
            throw new ConcurrentModificationException();
        }
        return this.totalCountToCurrentIndex < this.arrayTotalCount;
    }

    @Override
    public HistogramIterationValue next() {
        while (!this.exhaustedSubBuckets()) {
            this.countAtThisValue = this.histogram.getCountAtIndex(this.currentIndex);
            if (this.freshSubBucket) {
                this.totalCountToCurrentIndex += this.countAtThisValue;
                this.totalValueToCurrentIndex += this.countAtThisValue * this.histogram.highestEquivalentValue(this.currentValueAtIndex);
                this.freshSubBucket = false;
            }
            if (this.reachedIterationLevel()) {
                long valueIteratedTo = this.getValueIteratedTo();
                this.currentIterationValue.set(valueIteratedTo, this.prevValueIteratedTo, this.countAtThisValue, this.totalCountToCurrentIndex - this.totalCountToPrevIndex, this.totalCountToCurrentIndex, this.totalValueToCurrentIndex, 100.0 * (double)this.totalCountToCurrentIndex / (double)this.arrayTotalCount, this.getPercentileIteratedTo(), this.integerToDoubleValueConversionRatio);
                this.prevValueIteratedTo = valueIteratedTo;
                this.totalCountToPrevIndex = this.totalCountToCurrentIndex;
                this.incrementIterationLevel();
                if (this.histogram.getTotalCount() != this.arrayTotalCount) {
                    throw new ConcurrentModificationException();
                }
                return this.currentIterationValue;
            }
            this.incrementSubBucket();
        }
        if (this.histogram.getTotalCount() != this.arrayTotalCount || this.totalCountToCurrentIndex > this.arrayTotalCount) {
            throw new ConcurrentModificationException();
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    abstract void incrementIterationLevel();

    abstract boolean reachedIterationLevel();

    double getPercentileIteratedTo() {
        return 100.0 * (double)this.totalCountToCurrentIndex / (double)this.arrayTotalCount;
    }

    double getPercentileIteratedFrom() {
        return 100.0 * (double)this.totalCountToPrevIndex / (double)this.arrayTotalCount;
    }

    long getValueIteratedTo() {
        return this.histogram.highestEquivalentValue(this.currentValueAtIndex);
    }

    private boolean exhaustedSubBuckets() {
        return this.currentIndex >= this.histogram.countsArrayLength;
    }

    void incrementSubBucket() {
        this.freshSubBucket = true;
        ++this.currentIndex;
        this.currentValueAtIndex = this.histogram.valueFromIndex(this.currentIndex);
        this.nextValueAtIndex = this.histogram.valueFromIndex(this.currentIndex + 1);
    }
}

