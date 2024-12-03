/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.similarities.DefaultSimilarity
 */
package com.atlassian.confluence.internal.index.lucene;

import org.apache.lucene.search.similarities.DefaultSimilarity;

public class EdgeIndexSimilarity
extends DefaultSimilarity {
    public float coord(int overlap, int maxOverlap) {
        if ((float)overlap == 0.0f) {
            return 0.0f;
        }
        return 1.0f;
    }

    public float tf(float freq) {
        if (freq == 0.0f) {
            return 0.0f;
        }
        return 1.0f;
    }

    public float idf(long docFreq, long numDocs) {
        return 1.0f;
    }

    public float queryNorm(float sumOfSquaredWeights) {
        return 1.0f;
    }
}

