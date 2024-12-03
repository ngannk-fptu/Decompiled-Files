/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.DisjunctionScorer;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class DisjunctionMaxScorer
extends DisjunctionScorer {
    private final float tieBreakerMultiplier;
    private int doc = -1;
    private float scoreSum;
    private float scoreMax;

    public DisjunctionMaxScorer(Weight weight, float tieBreakerMultiplier, Similarity similarity, Scorer[] subScorers, int numScorers) {
        super(similarity, weight, subScorers, numScorers);
        this.tieBreakerMultiplier = tieBreakerMultiplier;
    }

    @Override
    public int nextDoc() throws IOException {
        if (this.numScorers == 0) {
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }
        while (this.subScorers[0].docID() == this.doc) {
            if (this.subScorers[0].nextDoc() != Integer.MAX_VALUE) {
                this.heapAdjust(0);
                continue;
            }
            this.heapRemoveRoot();
            if (this.numScorers != 0) continue;
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }
        this.doc = this.subScorers[0].docID();
        return this.doc;
    }

    @Override
    public int docID() {
        return this.doc;
    }

    @Override
    public float score() throws IOException {
        int doc = this.subScorers[0].docID();
        this.scoreSum = this.scoreMax = this.subScorers[0].score();
        int size = this.numScorers;
        this.scoreAll(1, size, doc);
        this.scoreAll(2, size, doc);
        return this.scoreMax + (this.scoreSum - this.scoreMax) * this.tieBreakerMultiplier;
    }

    private void scoreAll(int root, int size, int doc) throws IOException {
        if (root < size && this.subScorers[root].docID() == doc) {
            float sub = this.subScorers[root].score();
            this.scoreSum += sub;
            this.scoreMax = Math.max(this.scoreMax, sub);
            this.scoreAll((root << 1) + 1, size, doc);
            this.scoreAll((root << 1) + 2, size, doc);
        }
    }

    @Override
    public float freq() throws IOException {
        int doc = this.subScorers[0].docID();
        int size = this.numScorers;
        return 1 + this.freq(1, size, doc) + this.freq(2, size, doc);
    }

    private int freq(int root, int size, int doc) throws IOException {
        int freq = 0;
        if (root < size && this.subScorers[root].docID() == doc) {
            ++freq;
            freq += this.freq((root << 1) + 1, size, doc);
            freq += this.freq((root << 1) + 2, size, doc);
        }
        return freq;
    }

    @Override
    public int advance(int target) throws IOException {
        if (this.numScorers == 0) {
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }
        while (this.subScorers[0].docID() < target) {
            if (this.subScorers[0].advance(target) != Integer.MAX_VALUE) {
                this.heapAdjust(0);
                continue;
            }
            this.heapRemoveRoot();
            if (this.numScorers != 0) continue;
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }
        this.doc = this.subScorers[0].docID();
        return this.doc;
    }

    @Override
    public void visitSubScorers(Query parent, BooleanClause.Occur relationship, Scorer.ScorerVisitor<Query, Query, Scorer> visitor) {
        super.visitSubScorers(parent, relationship, visitor);
        Query q = this.weight.getQuery();
        for (int i = 0; i < this.numScorers; ++i) {
            this.subScorers[i].visitSubScorers(q, BooleanClause.Occur.SHOULD, visitor);
        }
    }
}

