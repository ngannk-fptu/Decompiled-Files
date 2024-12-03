/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.Scorer;

public class TotalHitCountCollector
extends Collector {
    private int totalHits;

    public int getTotalHits() {
        return this.totalHits;
    }

    public void setScorer(Scorer scorer) {
    }

    public void collect(int doc) {
        ++this.totalHits;
    }

    public void setNextReader(IndexReader reader, int docBase) {
    }

    public boolean acceptsDocsOutOfOrder() {
        return true;
    }
}

