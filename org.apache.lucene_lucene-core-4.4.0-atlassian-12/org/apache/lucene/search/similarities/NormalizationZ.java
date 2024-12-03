/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.Normalization;

public class NormalizationZ
extends Normalization {
    final float z;

    public NormalizationZ() {
        this(0.3f);
    }

    public NormalizationZ(float z) {
        this.z = z;
    }

    @Override
    public float tfn(BasicStats stats, float tf, float len) {
        return (float)((double)tf * Math.pow(stats.avgFieldLength / len, this.z));
    }

    @Override
    public String toString() {
        return "Z(" + this.z + ")";
    }

    public float getZ() {
        return this.z;
    }
}

