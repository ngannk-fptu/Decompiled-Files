/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.Normalization;

public class NormalizationH1
extends Normalization {
    private final float c;

    public NormalizationH1(float c) {
        this.c = c;
    }

    public NormalizationH1() {
        this(1.0f);
    }

    @Override
    public final float tfn(BasicStats stats, float tf, float len) {
        return tf * stats.getAvgFieldLength() / len;
    }

    @Override
    public String toString() {
        return "1";
    }

    public float getC() {
        return this.c;
    }
}

