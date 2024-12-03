/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.Iterator;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.DoubleHistogramIterationValue;
import org.HdrHistogram.LinearIterator;

public class DoubleLinearIterator
implements Iterator<DoubleHistogramIterationValue> {
    private final LinearIterator integerLinearIterator;
    private final DoubleHistogramIterationValue iterationValue;
    DoubleHistogram histogram;

    public void reset(double valueUnitsPerBucket) {
        this.integerLinearIterator.reset((long)(valueUnitsPerBucket * this.histogram.getDoubleToIntegerValueConversionRatio()));
    }

    public DoubleLinearIterator(DoubleHistogram histogram, double valueUnitsPerBucket) {
        this.histogram = histogram;
        this.integerLinearIterator = new LinearIterator(histogram.integerValuesHistogram, (long)(valueUnitsPerBucket * histogram.getDoubleToIntegerValueConversionRatio()));
        this.iterationValue = new DoubleHistogramIterationValue(this.integerLinearIterator.currentIterationValue);
    }

    @Override
    public boolean hasNext() {
        return this.integerLinearIterator.hasNext();
    }

    @Override
    public DoubleHistogramIterationValue next() {
        this.integerLinearIterator.next();
        return this.iterationValue;
    }

    @Override
    public void remove() {
        this.integerLinearIterator.remove();
    }
}

