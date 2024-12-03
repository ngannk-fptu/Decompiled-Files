/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.Iterator;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.DoubleHistogramIterationValue;
import org.HdrHistogram.RecordedValuesIterator;

public class DoubleRecordedValuesIterator
implements Iterator<DoubleHistogramIterationValue> {
    private final RecordedValuesIterator integerRecordedValuesIterator;
    private final DoubleHistogramIterationValue iterationValue;
    DoubleHistogram histogram;

    public void reset() {
        this.integerRecordedValuesIterator.reset();
    }

    public DoubleRecordedValuesIterator(DoubleHistogram histogram) {
        this.histogram = histogram;
        this.integerRecordedValuesIterator = new RecordedValuesIterator(histogram.integerValuesHistogram);
        this.iterationValue = new DoubleHistogramIterationValue(this.integerRecordedValuesIterator.currentIterationValue);
    }

    @Override
    public boolean hasNext() {
        return this.integerRecordedValuesIterator.hasNext();
    }

    @Override
    public DoubleHistogramIterationValue next() {
        this.integerRecordedValuesIterator.next();
        return this.iterationValue;
    }

    @Override
    public void remove() {
        this.integerRecordedValuesIterator.remove();
    }
}

