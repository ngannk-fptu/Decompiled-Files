/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.Iterator;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.AbstractHistogramIterator;
import org.HdrHistogram.HistogramIterationValue;

public class PercentileIterator
extends AbstractHistogramIterator
implements Iterator<HistogramIterationValue> {
    int percentileTicksPerHalfDistance;
    double percentileLevelToIterateTo;
    double percentileLevelToIterateFrom;
    boolean reachedLastRecordedValue;

    public void reset(int percentileTicksPerHalfDistance) {
        this.reset(this.histogram, percentileTicksPerHalfDistance);
    }

    private void reset(AbstractHistogram histogram, int percentileTicksPerHalfDistance) {
        super.resetIterator(histogram);
        this.percentileTicksPerHalfDistance = percentileTicksPerHalfDistance;
        this.percentileLevelToIterateTo = 0.0;
        this.percentileLevelToIterateFrom = 0.0;
        this.reachedLastRecordedValue = false;
    }

    public PercentileIterator(AbstractHistogram histogram, int percentileTicksPerHalfDistance) {
        this.reset(histogram, percentileTicksPerHalfDistance);
    }

    @Override
    public boolean hasNext() {
        if (super.hasNext()) {
            return true;
        }
        if (!this.reachedLastRecordedValue && this.arrayTotalCount > 0L) {
            this.percentileLevelToIterateTo = 100.0;
            this.reachedLastRecordedValue = true;
            return true;
        }
        return false;
    }

    @Override
    void incrementIterationLevel() {
        this.percentileLevelToIterateFrom = this.percentileLevelToIterateTo;
        long percentileReportingTicks = (long)this.percentileTicksPerHalfDistance * (long)Math.pow(2.0, (long)(Math.log(100.0 / (100.0 - this.percentileLevelToIterateTo)) / Math.log(2.0)) + 1L);
        this.percentileLevelToIterateTo += 100.0 / (double)percentileReportingTicks;
    }

    @Override
    boolean reachedIterationLevel() {
        if (this.countAtThisValue == 0L) {
            return false;
        }
        double currentPercentile = 100.0 * (double)this.totalCountToCurrentIndex / (double)this.arrayTotalCount;
        return currentPercentile >= this.percentileLevelToIterateTo;
    }

    @Override
    double getPercentileIteratedTo() {
        return this.percentileLevelToIterateTo;
    }

    @Override
    double getPercentileIteratedFrom() {
        return this.percentileLevelToIterateFrom;
    }
}

