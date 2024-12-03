/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.Distribution;

public class DistributionLL
extends Distribution {
    @Override
    public final float score(BasicStats stats, float tfn, float lambda) {
        return (float)(-Math.log(lambda / (tfn + lambda)));
    }

    @Override
    public String toString() {
        return "LL";
    }
}

