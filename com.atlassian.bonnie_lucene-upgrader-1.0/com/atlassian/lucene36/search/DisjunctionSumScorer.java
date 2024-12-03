/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.DisjunctionScorer;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Weight;
import java.io.IOException;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class DisjunctionSumScorer
extends DisjunctionScorer {
    private final int minimumNrMatchers;
    private int doc = -1;
    protected int nrMatchers = -1;
    private double score = Double.NaN;

    public DisjunctionSumScorer(Weight weight, List<Scorer> subScorers, int minimumNrMatchers) throws IOException {
        super(null, weight, subScorers.toArray(new Scorer[subScorers.size()]), subScorers.size());
        if (minimumNrMatchers <= 0) {
            throw new IllegalArgumentException("Minimum nr of matchers must be positive");
        }
        if (this.numScorers <= 1) {
            throw new IllegalArgumentException("There must be at least 2 subScorers");
        }
        this.minimumNrMatchers = minimumNrMatchers;
    }

    public DisjunctionSumScorer(Weight weight, List<Scorer> subScorers) throws IOException {
        this(weight, subScorers, 1);
    }

    @Override
    public int nextDoc() throws IOException {
        assert (this.doc != Integer.MAX_VALUE);
        while (true) {
            if (this.subScorers[0].docID() == this.doc) {
                if (this.subScorers[0].nextDoc() != Integer.MAX_VALUE) {
                    this.heapAdjust(0);
                    continue;
                }
                this.heapRemoveRoot();
                if (this.numScorers >= this.minimumNrMatchers) continue;
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            }
            this.afterNext();
            if (this.nrMatchers >= this.minimumNrMatchers) break;
        }
        return this.doc;
    }

    private void afterNext() throws IOException {
        Scorer sub = this.subScorers[0];
        this.doc = sub.docID();
        if (this.doc == Integer.MAX_VALUE) {
            this.nrMatchers = Integer.MAX_VALUE;
        } else {
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
        return (float)this.score;
    }

    @Override
    public int docID() {
        return this.doc;
    }

    @Override
    public float freq() throws IOException {
        return this.nrMatchers;
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
        this.afterNext();
        if (this.nrMatchers >= this.minimumNrMatchers) {
            return this.doc;
        }
        return this.nextDoc();
    }
}

