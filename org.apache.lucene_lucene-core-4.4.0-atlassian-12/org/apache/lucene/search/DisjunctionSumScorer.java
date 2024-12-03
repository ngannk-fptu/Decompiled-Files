/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.search.DisjunctionScorer;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

class DisjunctionSumScorer
extends DisjunctionScorer {
    private int doc = -1;
    protected int nrMatchers = -1;
    protected double score = Double.NaN;
    private final float[] coord;

    DisjunctionSumScorer(Weight weight, Scorer[] subScorers, float[] coord) throws IOException {
        super(weight, subScorers, subScorers.length);
        if (this.numScorers <= 1) {
            throw new IllegalArgumentException("There must be at least 2 subScorers");
        }
        this.coord = coord;
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

    private void afterNext() throws IOException {
        Scorer sub = this.subScorers[0];
        this.doc = sub.docID();
        if (this.doc != Integer.MAX_VALUE) {
            this.score = sub.score();
            this.nrMatchers = 1;
            this.countMatches(1);
            this.countMatches(2);
        }
    }

    private void countMatches(int root) throws IOException {
        if (root < this.numScorers && this.subScorers[root].docID() == this.doc) {
            ++this.nrMatchers;
            this.score += (double)this.subScorers[root].score();
            this.countMatches((root << 1) + 1);
            this.countMatches((root << 1) + 2);
        }
    }

    @Override
    public float score() throws IOException {
        return (float)this.score * this.coord[this.nrMatchers];
    }

    @Override
    public int docID() {
        return this.doc;
    }

    @Override
    public int freq() throws IOException {
        return this.nrMatchers;
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

