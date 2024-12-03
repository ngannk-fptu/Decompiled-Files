/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.BasicModel;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class BasicModelG
extends BasicModel {
    @Override
    public final float score(BasicStats stats, float tfn) {
        double F = stats.getTotalTermFreq() + 1L;
        double N = stats.getNumberOfDocuments();
        double lambda = F / (N + F);
        return (float)(SimilarityBase.log2(lambda + 1.0) + (double)tfn * SimilarityBase.log2((1.0 + lambda) / lambda));
    }

    @Override
    public String toString() {
        return "G";
    }
}

