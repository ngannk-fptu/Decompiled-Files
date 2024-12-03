/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.Distribution;

public class DistributionSPL
extends Distribution {
    @Override
    public final float score(BasicStats stats, float tfn, float lambda) {
        if (lambda == 1.0f) {
            lambda = 0.99f;
        }
        return (float)(-Math.log((Math.pow(lambda, tfn / (tfn + 1.0f)) - (double)lambda) / (double)(1.0f - lambda)));
    }

    @Override
    public String toString() {
        return "SPL";
    }
}

