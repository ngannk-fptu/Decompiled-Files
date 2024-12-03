/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.PhrasePositions;
import com.atlassian.lucene36.search.PhraseQuery;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import java.io.IOException;

abstract class PhraseScorer
extends Scorer {
    protected byte[] norms;
    protected float value;
    PhrasePositions min;
    PhrasePositions max;
    private float freq;

    PhraseScorer(Weight weight, PhraseQuery.PostingsAndFreq[] postings, Similarity similarity, byte[] norms) {
        super(similarity, weight);
        this.norms = norms;
        this.value = weight.getValue();
        if (postings.length > 0) {
            this.max = this.min = new PhrasePositions(postings[0].postings, postings[0].position, 0, postings[0].terms);
            this.max.doc = -1;
            for (int i = 1; i < postings.length; ++i) {
                PhrasePositions pp;
                this.max.next = pp = new PhrasePositions(postings[i].postings, postings[i].position, i, postings[i].terms);
                this.max = pp;
                this.max.doc = -1;
            }
            this.max.next = this.min;
        }
    }

    public int docID() {
        return this.max.doc;
    }

    public int nextDoc() throws IOException {
        return this.advance(this.max.doc);
    }

    private boolean advanceMin(int target) throws IOException {
        if (!this.min.skipTo(target)) {
            this.max.doc = Integer.MAX_VALUE;
            return false;
        }
        this.min = this.min.next;
        this.max = this.max.next;
        return true;
    }

    public float score() throws IOException {
        float raw = this.getSimilarity().tf(this.freq) * this.value;
        return this.norms == null ? raw : raw * this.getSimilarity().decodeNormValue(this.norms[this.max.doc]);
    }

    public int advance(int target) throws IOException {
        this.freq = 0.0f;
        if (!this.advanceMin(target)) {
            return Integer.MAX_VALUE;
        }
        boolean restart = false;
        while (this.freq == 0.0f) {
            while (this.min.doc < this.max.doc || restart) {
                restart = false;
                if (this.advanceMin(this.max.doc)) continue;
                return Integer.MAX_VALUE;
            }
            this.freq = this.phraseFreq();
            restart = true;
        }
        return this.max.doc;
    }

    public final float freq() {
        return this.freq;
    }

    abstract float phraseFreq() throws IOException;

    public String toString() {
        return "scorer(" + this.weight + ")";
    }
}

