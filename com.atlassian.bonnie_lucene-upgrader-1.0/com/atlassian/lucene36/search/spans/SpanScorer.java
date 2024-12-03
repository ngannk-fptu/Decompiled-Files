/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.search.spans.Spans;
import java.io.IOException;

public class SpanScorer
extends Scorer {
    protected Spans spans;
    protected byte[] norms;
    protected float value;
    protected boolean more = true;
    protected int doc;
    protected float freq;

    protected SpanScorer(Spans spans, Weight weight, Similarity similarity, byte[] norms) throws IOException {
        super(similarity, weight);
        this.spans = spans;
        this.norms = norms;
        this.value = weight.getValue();
        if (this.spans.next()) {
            this.doc = -1;
        } else {
            this.doc = Integer.MAX_VALUE;
            this.more = false;
        }
    }

    public int nextDoc() throws IOException {
        if (!this.setFreqCurrentDoc()) {
            this.doc = Integer.MAX_VALUE;
        }
        return this.doc;
    }

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
        do {
            int matchLength = this.spans.end() - this.spans.start();
            this.freq += this.getSimilarity().sloppyFreq(matchLength);
            this.more = this.spans.next();
        } while (this.more && this.doc == this.spans.doc());
        return true;
    }

    public int docID() {
        return this.doc;
    }

    public float score() throws IOException {
        float raw = this.getSimilarity().tf(this.freq) * this.value;
        return this.norms == null ? raw : raw * this.getSimilarity().decodeNormValue(this.norms[this.doc]);
    }

    public float freq() throws IOException {
        return this.freq;
    }

    protected Explanation explain(int doc) throws IOException {
        Explanation tfExplanation = new Explanation();
        int expDoc = this.advance(doc);
        float phraseFreq = expDoc == doc ? this.freq : 0.0f;
        tfExplanation.setValue(this.getSimilarity().tf(phraseFreq));
        tfExplanation.setDescription("tf(phraseFreq=" + phraseFreq + ")");
        return tfExplanation;
    }
}

