/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.BasicModel;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class BasicModelP
extends BasicModel {
    protected static double LOG2_E = SimilarityBase.log2(Math.E);

    @Override
    public final float score(BasicStats stats, float tfn) {
        float lambda = (float)(stats.getTotalTermFreq() + 1L) / (float)(stats.getNumberOfDocuments() + 1L);
        return (float)((double)tfn * SimilarityBase.log2(tfn / lambda) + (double)(lambda + 1.0f / (12.0f * tfn) - tfn) * LOG2_E + 0.5 * SimilarityBase.log2(Math.PI * 2 * (double)tfn));
    }

    @Override
    public String toString() {
        return "P";
    }
}

