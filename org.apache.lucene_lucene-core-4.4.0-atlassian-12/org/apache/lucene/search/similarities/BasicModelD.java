/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.BasicModel;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class BasicModelD
extends BasicModel {
    @Override
    public final float score(BasicStats stats, float tfn) {
        double F = (float)(stats.getTotalTermFreq() + 1L) + tfn;
        double phi = (double)tfn / F;
        double nphi = 1.0 - phi;
        double p = 1.0 / (double)(stats.getNumberOfDocuments() + 1L);
        double D = phi * SimilarityBase.log2(phi / p) + nphi * SimilarityBase.log2(nphi / (1.0 - p));
        return (float)(D * F + 0.5 * SimilarityBase.log2(1.0 + Math.PI * 2 * (double)tfn * nphi));
    }

    @Override
    public String toString() {
        return "D";
    }
}

