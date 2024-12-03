/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.Iterator;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.AbstractHistogramIterator;
import org.HdrHistogram.HistogramIterationValue;

public class LinearIterator
extends AbstractHistogramIterator
implements Iterator<HistogramIterationValue> {
    private long valueUnitsPerBucket;
    private long currentStepHighestValueReportingLevel;
    private long currentStepLowestValueReportingLevel;

    public void reset(long valueUnitsPerBucket) {
        this.reset(this.histogram, valueUnitsPerBucket);
    }

    private void reset(AbstractHistogram histogram, long valueUnitsPerBucket) {
        super.resetIterator(histogram);
        this.valueUnitsPerBucket = valueUnitsPerBucket;
        this.currentStepHighestValueReportingLevel = valueUnitsPerBucket - 1L;
        this.currentStepLowestValueReportingLevel = histogram.lowestEquivalentValue(this.currentStepHighestValueReportingLevel);
    }

    public LinearIterator(AbstractHistogram histogram, long valueUnitsPerBucket) {
        this.reset(histogram, valueUnitsPerBucket);
    }

    @Override
    public boolean hasNext() {
        if (super.hasNext()) {
            return true;
        }
        return this.currentStepHighestValueReportingLevel < this.nextValueAtIndex;
    }

    @Override
    void incrementIterationLevel() {
        this.currentStepHighestValueReportingLevel += this.valueUnitsPerBucket;
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

