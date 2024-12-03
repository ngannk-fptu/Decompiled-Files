/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.Iterator;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.DoubleHistogramIterationValue;
import org.HdrHistogram.LogarithmicIterator;

public class DoubleLogarithmicIterator
implements Iterator<DoubleHistogramIterationValue> {
    private final LogarithmicIterator integerLogarithmicIterator;
    private final DoubleHistogramIterationValue iterationValue;
    DoubleHistogram histogram;

    public void reset(double valueUnitsInFirstBucket, double logBase) {
        this.integerLogarithmicIterator.reset((long)(valueUnitsInFirstBucket * this.histogram.getDoubleToIntegerValueConversionRatio()), logBase);
    }

    public DoubleLogarithmicIterator(DoubleHistogram histogram, double valueUnitsInFirstBucket, double logBase) {
        this.histogram = histogram;
        this.integerLogarithmicIterator = new LogarithmicIterator(histogram.integerValuesHistogram, (long)(valueUnitsInFirstBucket * histogram.getDoubleToIntegerValueConversionRatio()), logBase);
        this.iterationValue = new DoubleHistogramIterationValue(this.integerLogarithmicIterator.currentIterationValue);
    }

    @Override
    public boolean hasNext() {
        return this.integerLogarithmicIterator.hasNext();
    }

    @Override
    public DoubleHistogramIterationValue next() {
        this.integerLogarithmicIterator.next();
        return this.iterationValue;
    }

    @Override
    public void remove() {
        this.integerLogarithmicIterator.remove();
    }
}

