/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.Distribution;
import org.apache.lucene.search.similarities.Lambda;
import org.apache.lucene.search.similarities.Normalization;
import org.apache.lucene.search.similarities.SimilarityBase;

public class IBSimilarity
extends SimilarityBase {
    protected final Distribution distribution;
    protected final Lambda lambda;
    protected final Normalization normalization;

    public IBSimilarity(Distribution distribution, Lambda lambda, Normalization normalization) {
        this.distribution = distribution;
        this.lambda = lambda;
        this.normalization = normalization;
    }

    @Override
    protected float score(BasicStats stats, float freq, float docLen) {
        return stats.getTotalBoost() * this.distribution.score(stats, this.normalization.tfn(stats, freq, docLen), this.lambda.lambda(stats));
    }

    @Override
    protected void explain(Explanation expl, BasicStats stats, int doc, float freq, float docLen) {
        if (stats.getTotalBoost() != 1.0f) {
            expl.addDetail(new Explanation(stats.getTotalBoost(), "boost"));
        }
        Explanation normExpl = this.normalization.explain(stats, freq, docLen);
        Explanation lambdaExpl = this.lambda.explain(stats);
        expl.addDetail(normExpl);
        expl.addDetail(lambdaExpl);
        expl.addDetail(this.distribution.explain(stats, normExpl.getValue(), lambdaExpl.getValue()));
    }

    @Override
    public String toString() {
        return "IB " + this.distribution.toString() + "-" + this.lambda.toString() + this.normalization.toString();
    }

    public Distribution getDistribution() {
        return this.distribution;
    }

    public Lambda getLambda() {
        return this.lambda;
    }

    public Normalization getNormalization() {
        return this.normalization;
    }
}

