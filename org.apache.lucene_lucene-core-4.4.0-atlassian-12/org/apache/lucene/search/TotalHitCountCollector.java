/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;

public class TotalHitCountCollector
extends Collector {
    private int totalHits;

    public int getTotalHits() {
        return this.totalHits;
    }

    @Override
    public void setScorer(Scorer scorer) {
    }

    @Override
    public void collect(int doc) {
        ++this.totalHits;
    }

    @Override
    public void setNextReader(AtomicReaderContext context) {
    }

    @Override
    public boolean acceptsDocsOutOfOrder() {
        return true;
    }
}

