/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.AbstractHistogramIterator;
import org.HdrHistogram.HistogramIterationValue;

public class AllValuesIterator
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

    public AllValuesIterator(AbstractHistogram histogram) {
        this.reset(histogram);
    }

    @Override
    void incrementIterationLevel() {
        this.visitedIndex = this.currentIndex;
    }

    @Override
    boolean reachedIterationLevel() {
        return this.visitedIndex != this.currentIndex;
    }

    @Override
    public boolean hasNext() {
        if (this.histogram.getTotalCount() != this.arrayTotalCount) {
            throw new ConcurrentModificationException();
        }
        return this.currentIndex < this.histogram.countsArrayLength - 1;
    }
}

