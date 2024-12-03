/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import java.util.Locale;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;

public class LMDirichletSimilarity
extends LMSimilarity {
    private final float mu;

    public LMDirichletSimilarity(LMSimilarity.CollectionModel collectionModel, float mu) {
        super(collectionModel);
        this.mu = mu;
    }

    public LMDirichletSimilarity(float mu) {
        this.mu = mu;
    }

    public LMDirichletSimilarity(LMSimilarity.CollectionModel collectionModel) {
        this(collectionModel, 2000.0f);
    }

    public LMDirichletSimilarity() {
        this(2000.0f);
    }

    @Override
    protected float score(BasicStats stats, float freq, float docLen) {
        float score = stats.getTotalBoost() * (float)(Math.log(1.0f + freq / (this.mu * ((LMSimilarity.LMStats)stats).getCollectionProbability())) + Math.log(this.mu / (docLen + this.mu)));
        return score > 0.0f ? score : 0.0f;
    }

    @Override
    protected void explain(Explanation expl, BasicStats stats, int doc, float freq, float docLen) {
        if (stats.getTotalBoost() != 1.0f) {
            expl.addDetail(new Explanation(stats.getTotalBoost(), "boost"));
        }
        expl.addDetail(new Explanation(this.mu, "mu"));
        Explanation weightExpl = new Explanation();
        weightExpl.setValue((float)Math.log(1.0f + freq / (this.mu * ((LMSimilarity.LMStats)stats).getCollectionProbability())));
        weightExpl.setDescription("term weight");
        expl.addDetail(weightExpl);
        expl.addDetail(new Explanation((float)Math.log(this.mu / (docLen + this.mu)), "document norm"));
        super.explain(expl, stats, doc, freq, docLen);
    }

    public float getMu() {
        return this.mu;
    }

    @Override
    public String getName() {
        return String.format(Locale.ROOT, "Dirichlet(%f)", Float.valueOf(this.getMu()));
    }
}

