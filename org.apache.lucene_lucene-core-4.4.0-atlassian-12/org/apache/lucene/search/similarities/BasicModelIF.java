/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.BasicModel;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class BasicModelIF
extends BasicModel {
    @Override
    public final float score(BasicStats stats, float tfn) {
        long N = stats.getNumberOfDocuments();
        long F = stats.getTotalTermFreq();
        return tfn * (float)SimilarityBase.log2(1.0 + (double)(N + 1L) / ((double)F + 0.5));
    }

    @Override
    public String toString() {
        return "I(F)";
    }
}

