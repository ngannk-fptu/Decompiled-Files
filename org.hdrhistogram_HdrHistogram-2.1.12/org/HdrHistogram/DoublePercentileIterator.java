/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.Iterator;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.DoubleHistogramIterationValue;
import org.HdrHistogram.PercentileIterator;

public class DoublePercentileIterator
implements Iterator<DoubleHistogramIterationValue> {
    private final PercentileIterator integerPercentileIterator;
    private final DoubleHistogramIterationValue iterationValue;
    DoubleHistogram histogram;

    public void reset(int percentileTicksPerHalfDistance) {
        this.integerPercentileIterator.reset(percentileTicksPerHalfDistance);
    }

    public DoublePercentileIterator(DoubleHistogram histogram, int percentileTicksPerHalfDistance) {
        this.histogram = histogram;
        this.integerPercentileIterator = new PercentileIterator(histogram.integerValuesHistogram, percentileTicksPerHalfDistance);
        this.iterationValue = new DoubleHistogramIterationValue(this.integerPercentileIterator.currentIterationValue);
    }

    @Override
    public boolean hasNext() {
        return this.integerPercentileIterator.hasNext();
    }

    @Override
    public DoubleHistogramIterationValue next() {
        this.integerPercentileIterator.next();
        return this.iterationValue;
    }

    @Override
    public void remove() {
        this.integerPercentileIterator.remove();
    }
}

