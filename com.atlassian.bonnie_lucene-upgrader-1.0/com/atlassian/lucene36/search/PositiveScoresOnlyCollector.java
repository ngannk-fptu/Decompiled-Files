/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.ScoreCachingWrappingScorer;
import com.atlassian.lucene36.search.Scorer;
import java.io.IOException;

public class PositiveScoresOnlyCollector
extends Collector {
    private final Collector c;
    private Scorer scorer;

    public PositiveScoresOnlyCollector(Collector c) {
        this.c = c;
    }

    public void collect(int doc) throws IOException {
        if (this.scorer.score() > 0.0f) {
            this.c.collect(doc);
        }
    }

    public void setNextReader(IndexReader reader, int docBase) throws IOException {
        this.c.setNextReader(reader, docBase);
    }

    public void setScorer(Scorer scorer) throws IOException {
        this.scorer = new ScoreCachingWrappingScorer(scorer);
        this.c.setScorer(this.scorer);
    }

    public boolean acceptsDocsOutOfOrder() {
        return this.c.acceptsDocsOutOfOrder();
    }
}

