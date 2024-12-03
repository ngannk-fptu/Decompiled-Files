/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.Iterator;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.AbstractHistogramIterator;
import org.HdrHistogram.HistogramIterationValue;

public class RecordedValuesIterator
extends AbstractHistogramIterator
implements Iterator<HistogramIterationValue> {
    int visitedIndex;

    public void reset() {
        this.reset(this.histogram);
    }

    private void reset(AbstractHistogram histogram) {
        super.resetIterator(histogram);
        this.visitedIndex = -1;
    }

    public RecordedValuesIterator(AbstractHistogram histogram) {
        this.reset(histogram);
    }

    @Override
    void incrementIterationLevel() {
        this.visitedIndex = this.currentIndex;
    }

    @Override
    boolean reachedIterationLevel() {
        long currentCount = this.histogram.getCountAtIndex(this.currentIndex);
        return currentCount != 0L && this.visitedIndex != this.currentIndex;
    }
}

