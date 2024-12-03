/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Similarity;
import java.io.IOException;

public class ScoreCachingWrappingScorer
extends Scorer {
    private final Scorer scorer;
    private int curDoc = -1;
    private float curScore;

    public ScoreCachingWrappingScorer(Scorer scorer) {
        super(scorer.getSimilarity(), scorer.weight);
        this.scorer = scorer;
    }

    protected boolean score(Collector collector, int max, int firstDocID) throws IOException {
        return this.scorer.score(collector, max, firstDocID);
    }

    public Similarity getSimilarity() {
        return this.scorer.getSimilarity();
    }

    public float score() throws IOException {
        int doc = this.scorer.docID();
        if (doc != this.curDoc) {
            this.curScore = this.scorer.score();
            this.curDoc = doc;
        }
        return this.curScore;
    }

    public int docID() {
        return this.scorer.docID();
    }

    public int nextDoc() throws IOException {
        return this.scorer.nextDoc();
    }

    public void score(Collector collector) throws IOException {
        this.scorer.score(collector);
    }

    public int advance(int target) throws IOException {
        return this.scorer.advance(target);
    }
}

