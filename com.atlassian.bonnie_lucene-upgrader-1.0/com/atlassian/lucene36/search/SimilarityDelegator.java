/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.FieldInvertState;
import com.atlassian.lucene36.search.Similarity;

@Deprecated
public class SimilarityDelegator
extends Similarity {
    private Similarity delegee;

    public SimilarityDelegator(Similarity delegee) {
        this.delegee = delegee;
    }

    public float computeNorm(String fieldName, FieldInvertState state) {
        return this.delegee.computeNorm(fieldName, state);
    }

    public float queryNorm(float sumOfSquaredWeights) {
        return this.delegee.queryNorm(sumOfSquaredWeights);
    }

    public float tf(float freq) {
        return this.delegee.tf(freq);
    }

    public float sloppyFreq(int distance) {
        return this.delegee.sloppyFreq(distance);
    }

    public float idf(int docFreq, int numDocs) {
        return this.delegee.idf(docFreq, numDocs);
    }

    public float coord(int overlap, int maxOverlap) {
        return this.delegee.coord(overlap, maxOverlap);
    }

    public float scorePayload(int docId, String fieldName, int start, int end, byte[] payload, int offset, int length) {
        return this.delegee.scorePayload(docId, fieldName, start, end, payload, offset, length);
    }
}

