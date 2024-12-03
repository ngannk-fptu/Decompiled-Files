/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;

public abstract class Distribution {
    public abstract float score(BasicStats var1, float var2, float var3);

    public Explanation explain(BasicStats stats, float tfn, float lambda) {
        return new Explanation(this.score(stats, tfn, lambda), this.getClass().getSimpleName());
    }

    public abstract String toString();
}

