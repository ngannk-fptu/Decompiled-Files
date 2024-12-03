/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.Normalization;

public class NormalizationH3
extends Normalization {
    private final float mu;

    public NormalizationH3() {
        this(800.0f);
    }

    public NormalizationH3(float mu) {
        this.mu = mu;
    }

    @Override
    public float tfn(BasicStats stats, float tf, float len) {
        return (tf + this.mu * (((float)stats.getTotalTermFreq() + 1.0f) / ((float)stats.getNumberOfFieldTokens() + 1.0f))) / (len + this.mu) * this.mu;
    }

    @Override
    public String toString() {
        return "3(" + this.mu + ")";
    }

    public float getMu() {
        return this.mu;
    }
}

