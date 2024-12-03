/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.ScoreCachingWrappingScorer;
import org.apache.lucene.search.Scorer;

public class PositiveScoresOnlyCollector
extends Collector {
    private final Collector c;
    private Scorer scorer;

    public PositiveScoresOnlyCollector(Collector c) {
        this.c = c;
    }

    @Override
    public void collect(int doc) throws IOException {
        if (this.scorer.score() > 0.0f) {
            this.c.collect(doc);
        }
    }

    @Override
    public void setNextReader(AtomicReaderContext context) throws IOException {
        this.c.setNextReader(context);
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
        this.scorer = new ScoreCachingWrappingScorer(scorer);
        this.c.setScorer(this.scorer);
    }

    @Override
    public boolean acceptsDocsOutOfOrder() {
        return this.c.acceptsDocsOutOfOrder();
    }
}

