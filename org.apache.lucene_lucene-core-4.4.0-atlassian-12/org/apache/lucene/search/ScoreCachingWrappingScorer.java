/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;

public class ScoreCachingWrappingScorer
extends Scorer {
    private final Scorer scorer;
    private int curDoc = -1;
    private float curScore;

    public ScoreCachingWrappingScorer(Scorer scorer) {
        super(scorer.weight);
        this.scorer = scorer;
    }

    @Override
    public boolean score(Collector collector, int max, int firstDocID) throws IOException {
        return this.scorer.score(collector, max, firstDocID);
    }

    @Override
    public float score() throws IOException {
        int doc = this.scorer.docID();
        if (doc != this.curDoc) {
            this.curScore = this.scorer.score();
            this.curDoc = doc;
        }
        return this.curScore;
    }

    @Override
    public int freq() throws IOException {
        return this.scorer.freq();
    }

    @Override
    public int docID() {
        return this.scorer.docID();
    }

    @Override
    public int nextDoc() throws IOException {
        return this.scorer.nextDoc();
    }

    @Override
    public void score(Collector collector) throws IOException {
        this.scorer.score(collector);
    }

    @Override
    public int advance(int target) throws IOException {
        return this.scorer.advance(target);
    }

    @Override
    public Collection<Scorer.ChildScorer> getChildren() {
        return Collections.singleton(new Scorer.ChildScorer(this.scorer, "CACHED"));
    }

    @Override
    public long cost() {
        return this.scorer.cost();
    }
}

