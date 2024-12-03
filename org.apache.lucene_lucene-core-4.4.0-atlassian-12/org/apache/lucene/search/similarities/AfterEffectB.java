/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.AfterEffect;
import org.apache.lucene.search.similarities.BasicStats;

public class AfterEffectB
extends AfterEffect {
    @Override
    public final float score(BasicStats stats, float tfn) {
        long F = stats.getTotalTermFreq() + 1L;
        long n = stats.getDocFreq() + 1L;
        return (float)(F + 1L) / ((float)n * (tfn + 1.0f));
    }

    @Override
    public final Explanation explain(BasicStats stats, float tfn) {
        Explanation result = new Explanation();
        result.setDescription(this.getClass().getSimpleName() + ", computed from: ");
        result.setValue(this.score(stats, tfn));
        result.addDetail(new Explanation(tfn, "tfn"));
        result.addDetail(new Explanation(stats.getTotalTermFreq(), "totalTermFreq"));
        result.addDetail(new Explanation(stats.getDocFreq(), "docFreq"));
        return result;
    }

    @Override
    public String toString() {
        return "B";
    }
}

