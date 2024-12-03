/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.AfterEffect;
import org.apache.lucene.search.similarities.BasicModel;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.Normalization;
import org.apache.lucene.search.similarities.SimilarityBase;

public class DFRSimilarity
extends SimilarityBase {
    protected final BasicModel basicModel;
    protected final AfterEffect afterEffect;
    protected final Normalization normalization;

    public DFRSimilarity(BasicModel basicModel, AfterEffect afterEffect, Normalization normalization) {
        if (basicModel == null || afterEffect == null || normalization == null) {
            throw new NullPointerException("null parameters not allowed.");
        }
        this.basicModel = basicModel;
        this.afterEffect = afterEffect;
        this.normalization = normalization;
    }

    @Override
    protected float score(BasicStats stats, float freq, float docLen) {
        float tfn = this.normalization.tfn(stats, freq, docLen);
        return stats.getTotalBoost() * this.basicModel.score(stats, tfn) * this.afterEffect.score(stats, tfn);
    }

    @Override
    protected void explain(Explanation expl, BasicStats stats, int doc, float freq, float docLen) {
        if (stats.getTotalBoost() != 1.0f) {
            expl.addDetail(new Explanation(stats.getTotalBoost(), "boost"));
        }
        Explanation normExpl = this.normalization.explain(stats, freq, docLen);
        float tfn = normExpl.getValue();
        expl.addDetail(normExpl);
        expl.addDetail(this.basicModel.explain(stats, tfn));
        expl.addDetail(this.afterEffect.explain(stats, tfn));
    }

    @Override
    public String toString() {
        return "DFR " + this.basicModel.toString() + this.afterEffect.toString() + this.normalization.toString();
    }

    public BasicModel getBasicModel() {
        return this.basicModel;
    }

    public AfterEffect getAfterEffect() {
        return this.afterEffect;
    }

    public Normalization getNormalization() {
        return this.normalization;
    }
}

