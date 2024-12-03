/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.Normalization;
import org.apache.lucene.search.similarities.SimilarityBase;

public class NormalizationH2
extends Normalization {
    private final float c;

    public NormalizationH2(float c) {
        this.c = c;
    }

    public NormalizationH2() {
        this(1.0f);
    }

    @Override
    public final float tfn(BasicStats stats, float tf, float len) {
        return (float)((double)tf * SimilarityBase.log2(1.0f + this.c * stats.getAvgFieldLength() / len));
    }

    @Override
    public String toString() {
        return "2";
    }

    public float getC() {
        return this.c;
    }
}

