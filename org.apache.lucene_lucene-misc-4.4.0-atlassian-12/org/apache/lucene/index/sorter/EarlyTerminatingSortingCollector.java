/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.CollectionTerminatedException
 *  org.apache.lucene.search.Collector
 *  org.apache.lucene.search.Scorer
 */
package org.apache.lucene.index.sorter;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.sorter.Sorter;
import org.apache.lucene.index.sorter.SortingMergePolicy;
import org.apache.lucene.search.CollectionTerminatedException;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;

public class EarlyTerminatingSortingCollector
extends Collector {
    protected final Collector in;
    protected final Sorter sorter;
    protected final int numDocsToCollect;
    protected int segmentTotalCollect;
    protected boolean segmentSorted;
    private int numCollected;

    public EarlyTerminatingSortingCollector(Collector in, Sorter sorter, int numDocsToCollect) {
        if (numDocsToCollect <= 0) {
            throw new IllegalStateException("numDocsToCollect must always be > 0, got " + this.segmentTotalCollect);
        }
        this.in = in;
        this.sorter = sorter;
        this.numDocsToCollect = numDocsToCollect;
    }

    public void setScorer(Scorer scorer) throws IOException {
        this.in.setScorer(scorer);
    }

    public void collect(int doc) throws IOException {
        this.in.collect(doc);
        if (++this.numCollected >= this.segmentTotalCollect) {
            throw new CollectionTerminatedException();
        }
    }

    public void setNextReader(AtomicReaderContext context) throws IOException {
        this.in.setNextReader(context);
        this.segmentSorted = SortingMergePolicy.isSorted(context.reader(), this.sorter);
        this.segmentTotalCollect = this.segmentSorted ? this.numDocsToCollect : Integer.MAX_VALUE;
        this.numCollected = 0;
    }

    public boolean acceptsDocsOutOfOrder() {
        return !this.segmentSorted && this.in.acceptsDocsOutOfOrder();
    }
}

