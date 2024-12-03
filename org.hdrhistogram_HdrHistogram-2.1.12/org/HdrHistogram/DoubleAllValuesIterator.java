/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.Iterator;
import org.HdrHistogram.AllValuesIterator;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.DoubleHistogramIterationValue;

public class DoubleAllValuesIterator
implements Iterator<DoubleHistogramIterationValue> {
    private final AllValuesIterator integerAllValuesIterator;
    private final DoubleHistogramIterationValue iterationValue;
    DoubleHistogram histogram;

    public void reset() {
        this.integerAllValuesIterator.reset();
    }

    public DoubleAllValuesIterator(DoubleHistogram histogram) {
        this.histogram = histogram;
        this.integerAllValuesIterator = new AllValuesIterator(histogram.integerValuesHistogram);
        this.iterationValue = new DoubleHistogramIterationValue(this.integerAllValuesIterator.currentIterationValue);
    }

    @Override
    public boolean hasNext() {
        return this.integerAllValuesIterator.hasNext();
    }

    @Override
    public DoubleHistogramIterationValue next() {
        this.integerAllValuesIterator.next();
        return this.iterationValue;
    }

    @Override
    public void remove() {
        this.integerAllValuesIterator.remove();
    }
}

