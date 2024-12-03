/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.search.DisjunctionScorer;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

class DisjunctionMaxScorer
extends DisjunctionScorer {
    private final float tieBreakerMultiplier;
    private int doc = -1;
    private int freq = -1;
    private float scoreSum;
    private float scoreMax;

    public DisjunctionMaxScorer(Weight weight, float tieBreakerMultiplier, Scorer[] subScorers, int numScorers) {
        super(weight, subScorers, numScorers);
        this.tieBreakerMultiplier = tieBreakerMultiplier;
    }

    @Override
    public int nextDoc() throws IOException {
        assert (this.doc != Integer.MAX_VALUE);
        do {
            if (this.subScorers[0].nextDoc() != Integer.MAX_VALUE) {
                this.heapAdjust(0);
                continue;
            }
            this.heapRemoveRoot();
            if (this.numScorers != 0) continue;
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        } while (this.subScorers[0].docID() == this.doc);
        this.afterNext();
        return this.doc;
    }

    @Override
    public int docID() {
        return this.doc;
    }

    @Override
    public float score() throws IOException {
        return this.scoreMax + (this.scoreSum - this.scoreMax) * this.tieBreakerMultiplier;
    }

    private void afterNext() throws IOException {
        this.doc = this.subScorers[0].docID();
        if (this.doc != Integer.MAX_VALUE) {
            this.scoreSum = this.scoreMax = this.subScorers[0].score();
            this.freq = 1;
            this.scoreAll(1);
            this.scoreAll(2);
        }
    }

    private void scoreAll(int root) throws IOException {
        if (root < this.numScorers && this.subScorers[root].docID() == this.doc) {
            float sub = this.subScorers[root].score();
            ++this.freq;
            this.scoreSum += sub;
            this.scoreMax = Math.max(this.scoreMax, sub);
            this.scoreAll((root << 1) + 1);
            this.scoreAll((root << 1) + 2);
        }
    }

    @Override
    public int freq() throws IOException {
        return this.freq;
    }

    @Override
    public int advance(int target) throws IOException {
        assert (this.doc != Integer.MAX_VALUE);
        do {
            if (this.subScorers[0].advance(target) != Integer.MAX_VALUE) {
                this.heapAdjust(0);
                continue;
            }
            this.heapRemoveRoot();
            if (this.numScorers != 0) continue;
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        } while (this.subScorers[0].docID() < target);
        this.afterNext();
        return this.doc;
    }
}

