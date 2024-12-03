/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicModel;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class BasicModelIn
extends BasicModel {
    @Override
    public final float score(BasicStats stats, float tfn) {
        long N = stats.getNumberOfDocuments();
        long n = stats.getDocFreq();
        return tfn * (float)SimilarityBase.log2((double)(N + 1L) / ((double)n + 0.5));
    }

    @Override
    public final Explanation explain(BasicStats stats, float tfn) {
        Explanation result = new Explanation();
        result.setDescription(this.getClass().getSimpleName() + ", computed from: ");
        result.setValue(this.score(stats, tfn));
        result.addDetail(new Explanation(tfn, "tfn"));
        result.addDetail(new Explanation(stats.getNumberOfDocuments(), "numberOfDocuments"));
        result.addDetail(new Explanation(stats.getDocFreq(), "docFreq"));
        return result;
    }

    @Override
    public String toString() {
        return "I(n)";
    }
}

