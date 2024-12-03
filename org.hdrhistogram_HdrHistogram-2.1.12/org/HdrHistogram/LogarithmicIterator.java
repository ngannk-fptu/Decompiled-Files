/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.Iterator;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.AbstractHistogramIterator;
import org.HdrHistogram.HistogramIterationValue;

public class LogarithmicIterator
extends AbstractHistogramIterator
implements Iterator<HistogramIterationValue> {
    long valueUnitsInFirstBucket;
    double logBase;
    double nextValueReportingLevel;
    long currentStepHighestValueReportingLevel;
    long currentStepLowestValueReportingLevel;

    public void reset(long valueUnitsInFirstBucket, double logBase) {
        this.reset(this.histogram, valueUnitsInFirstBucket, logBase);
    }

    private void reset(AbstractHistogram histogram, long valueUnitsInFirstBucket, double logBase) {
        super.resetIterator(histogram);
        this.logBase = logBase;
        this.valueUnitsInFirstBucket = valueUnitsInFirstBucket;
        this.nextValueReportingLevel = valueUnitsInFirstBucket;
        this.currentStepHighestValueReportingLevel = (long)this.nextValueReportingLevel - 1L;
        this.currentStepLowestValueReportingLevel = histogram.lowestEquivalentValue(this.currentStepHighestValueReportingLevel);
    }

    public LogarithmicIterator(AbstractHistogram histogram, long valueUnitsInFirstBucket, double logBase) {
        this.reset(histogram, valueUnitsInFirstBucket, logBase);
    }

    @Override
    public boolean hasNext() {
        if (super.hasNext()) {
            return true;
        }
        return this.histogram.lowestEquivalentValue((long)this.nextValueReportingLevel) < this.nextValueAtIndex;
    }

    @Override
    void incrementIterationLevel() {
        this.nextValueReportingLevel *= this.logBase;
        this.currentStepHighestValueReportingLevel = (long)this.nextValueReportingLevel - 1L;
        this.currentStepLowestValueReportingLevel = this.histogram.lowestEquivalentValue(this.currentStepHighestValueReportingLevel);
    }

    @Override
    long getValueIteratedTo() {
        return this.currentStepHighestValueReportingLevel;
    }

    @Override
    boolean reachedIterationLevel() {
        return this.currentValueAtIndex >= this.currentStepLowestValueReportingLevel || this.currentIndex >= this.histogram.countsArrayLength - 1;
    }
}

