/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.spans.Spans;

public class SpanScorer
extends Scorer {
    protected Spans spans;
    protected boolean more = true;
    protected int doc;
    protected float freq;
    protected int numMatches;
    protected final Similarity.SimScorer docScorer;

    protected SpanScorer(Spans spans, Weight weight, Similarity.SimScorer docScorer) throws IOException {
        super(weight);
        this.docScorer = docScorer;
        this.spans = spans;
        this.doc = -1;
        this.more = spans.next();
    }

    @Override
    public int nextDoc() throws IOException {
        if (!this.setFreqCurrentDoc()) {
            this.doc = Integer.MAX_VALUE;
        }
        return this.doc;
    }

    @Override
    public int advance(int target) throws IOException {
        if (!this.more) {
            this.doc = Integer.MAX_VALUE;
            return Integer.MAX_VALUE;
        }
        if (this.spans.doc() < target) {
            this.more = this.spans.skipTo(target);
        }
        if (!this.setFreqCurrentDoc()) {
            this.doc = Integer.MAX_VALUE;
        }
        return this.doc;
    }

    protected boolean setFreqCurrentDoc() throws IOException {
        if (!this.more) {
            return false;
        }
        this.doc = this.spans.doc();
        this.freq = 0.0f;
        this.numMatches = 0;
        do {
            int matchLength = this.spans.end() - this.spans.start();
            this.freq += this.docScorer.computeSlopFactor(matchLength);
            ++this.numMatches;
            this.more = this.spans.next();
        } while (this.more && this.doc == this.spans.doc());
        return true;
    }

    @Override
    public int docID() {
        return this.doc;
    }

    @Override
    public float score() throws IOException {
        return this.docScorer.score(this.doc, this.freq);
    }

    @Override
    public int freq() throws IOException {
        return this.numMatches;
    }

    public float sloppyFreq() throws IOException {
        return this.freq;
    }

    @Override
    public long cost() {
        return this.spans.cost();
    }
}

