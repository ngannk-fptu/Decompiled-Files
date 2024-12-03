/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import java.util.Locale;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;

public class LMJelinekMercerSimilarity
extends LMSimilarity {
    private final float lambda;

    public LMJelinekMercerSimilarity(LMSimilarity.CollectionModel collectionModel, float lambda) {
        super(collectionModel);
        this.lambda = lambda;
    }

    public LMJelinekMercerSimilarity(float lambda) {
        this.lambda = lambda;
    }

    @Override
    protected float score(BasicStats stats, float freq, float docLen) {
        return stats.getTotalBoost() * (float)Math.log(1.0f + (1.0f - this.lambda) * freq / docLen / (this.lambda * ((LMSimilarity.LMStats)stats).getCollectionProbability()));
    }

    @Override
    protected void explain(Explanation expl, BasicStats stats, int doc, float freq, float docLen) {
        if (stats.getTotalBoost() != 1.0f) {
            expl.addDetail(new Explanation(stats.getTotalBoost(), "boost"));
        }
        expl.addDetail(new Explanation(this.lambda, "lambda"));
        super.explain(expl, stats, doc, freq, docLen);
    }

    public float getLambda() {
        return this.lambda;
    }

    @Override
    public String getName() {
        return String.format(Locale.ROOT, "Jelinek-Mercer(%f)", Float.valueOf(this.getLambda()));
    }
}

