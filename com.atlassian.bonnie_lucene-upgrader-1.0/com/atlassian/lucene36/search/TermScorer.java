/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import java.io.IOException;

final class TermScorer
extends Scorer {
    private final TermDocs termDocs;
    private final byte[] norms;
    private float weightValue;
    private int doc = -1;
    private int freq;
    private final int[] docs = new int[32];
    private final int[] freqs = new int[32];
    private int pointer;
    private int pointerMax;
    private static final int SCORE_CACHE_SIZE = 32;
    private final float[] scoreCache = new float[32];

    TermScorer(Weight weight, TermDocs td, Similarity similarity, byte[] norms) {
        super(similarity, weight);
        this.termDocs = td;
        this.norms = norms;
        this.weightValue = weight.getValue();
        for (int i = 0; i < 32; ++i) {
            this.scoreCache[i] = this.getSimilarity().tf(i) * this.weightValue;
        }
    }

    public void score(Collector c) throws IOException {
        this.score(c, Integer.MAX_VALUE, this.nextDoc());
    }

    protected boolean score(Collector c, int end, int firstDocID) throws IOException {
        c.setScorer(this);
        while (this.doc < end) {
            c.collect(this.doc);
            if (++this.pointer >= this.pointerMax) {
                this.pointerMax = this.termDocs.read(this.docs, this.freqs);
                if (this.pointerMax != 0) {
                    this.pointer = 0;
                } else {
                    this.termDocs.close();
                    this.doc = Integer.MAX_VALUE;
                    return false;
                }
            }
            this.doc = this.docs[this.pointer];
            this.freq = this.freqs[this.pointer];
        }
        return true;
    }

    public int docID() {
        return this.doc;
    }

    public float freq() {
        return this.freq;
    }

    public int nextDoc() throws IOException {
        ++this.pointer;
        if (this.pointer >= this.pointerMax) {
            this.pointerMax = this.termDocs.read(this.docs, this.freqs);
            if (this.pointerMax != 0) {
                this.pointer = 0;
            } else {
                this.termDocs.close();
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            }
        }
        this.doc = this.docs[this.pointer];
        this.freq = this.freqs[this.pointer];
        return this.doc;
    }

    public float score() {
        assert (this.doc != -1);
        float raw = this.freq < 32 ? this.scoreCache[this.freq] : this.getSimilarity().tf(this.freq) * this.weightValue;
        return this.norms == null ? raw : raw * this.getSimilarity().decodeNormValue(this.norms[this.doc]);
    }

    public int advance(int target) throws IOException {
        ++this.pointer;
        while (this.pointer < this.pointerMax) {
            if (this.docs[this.pointer] >= target) {
                this.freq = this.freqs[this.pointer];
                this.doc = this.docs[this.pointer];
                return this.doc;
            }
            ++this.pointer;
        }
        boolean result = this.termDocs.skipTo(target);
        if (result) {
            this.pointerMax = 1;
            this.pointer = 0;
            this.docs[this.pointer] = this.doc = this.termDocs.doc();
            this.freqs[this.pointer] = this.freq = this.termDocs.freq();
        } else {
            this.doc = Integer.MAX_VALUE;
        }
        return this.doc;
    }

    public String toString() {
        return "scorer(" + this.weight + ")";
    }
}

