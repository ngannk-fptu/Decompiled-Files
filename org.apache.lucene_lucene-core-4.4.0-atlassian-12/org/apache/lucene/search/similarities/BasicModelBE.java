/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.BasicModel;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class BasicModelBE
extends BasicModel {
    @Override
    public final float score(BasicStats stats, float tfn) {
        double F = (float)(stats.getTotalTermFreq() + 1L) + tfn;
        double N = F + (double)stats.getNumberOfDocuments();
        return (float)(-SimilarityBase.log2((N - 1.0) * Math.E) + this.f(N + F - 1.0, N + F - (double)tfn - 2.0) - this.f(F, F - (double)tfn));
    }

    private final double f(double n, double m) {
        return (m + 0.5) * SimilarityBase.log2(n / m) + (n - m) * SimilarityBase.log2(n);
    }

    @Override
    public String toString() {
        return "Be";
    }
}

