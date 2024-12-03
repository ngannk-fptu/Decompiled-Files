/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.FieldInvertState;
import com.atlassian.lucene36.search.Similarity;

public class DefaultSimilarity
extends Similarity {
    protected boolean discountOverlaps = true;

    public float computeNorm(String field, FieldInvertState state) {
        int numTerms = this.discountOverlaps ? state.getLength() - state.getNumOverlap() : state.getLength();
        return state.getBoost() * (float)(1.0 / Math.sqrt(numTerms));
    }

    public float queryNorm(float sumOfSquaredWeights) {
        return (float)(1.0 / Math.sqrt(sumOfSquaredWeights));
    }

    public float tf(float freq) {
        return (float)Math.sqrt(freq);
    }

    public float sloppyFreq(int distance) {
        return 1.0f / (float)(distance + 1);
    }

    public float idf(int docFreq, int numDocs) {
        return (float)(Math.log((double)numDocs / (double)(docFreq + 1)) + 1.0);
    }

    public float coord(int overlap, int maxOverlap) {
        return (float)overlap / (float)maxOverlap;
    }

    public void setDiscountOverlaps(boolean v) {
        this.discountOverlaps = v;
    }

    public boolean getDiscountOverlaps() {
        return this.discountOverlaps;
    }
}

