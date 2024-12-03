/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import java.util.Locale;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public abstract class LMSimilarity
extends SimilarityBase {
    protected final CollectionModel collectionModel;

    public LMSimilarity(CollectionModel collectionModel) {
        this.collectionModel = collectionModel;
    }

    public LMSimilarity() {
        this(new DefaultCollectionModel());
    }

    @Override
    protected BasicStats newStats(String field, float queryBoost) {
        return new LMStats(field, queryBoost);
    }

    @Override
    protected void fillBasicStats(BasicStats stats, CollectionStatistics collectionStats, TermStatistics termStats) {
        super.fillBasicStats(stats, collectionStats, termStats);
        LMStats lmStats = (LMStats)stats;
        lmStats.setCollectionProbability(this.collectionModel.computeProbability(stats));
    }

    @Override
    protected void explain(Explanation expl, BasicStats stats, int doc, float freq, float docLen) {
        expl.addDetail(new Explanation(this.collectionModel.computeProbability(stats), "collection probability"));
    }

    public abstract String getName();

    @Override
    public String toString() {
        String coll = this.collectionModel.getName();
        if (coll != null) {
            return String.format(Locale.ROOT, "LM %s - %s", this.getName(), coll);
        }
        return String.format(Locale.ROOT, "LM %s", this.getName());
    }

    public static class DefaultCollectionModel
    implements CollectionModel {
        @Override
        public float computeProbability(BasicStats stats) {
            return ((float)stats.getTotalTermFreq() + 1.0f) / ((float)stats.getNumberOfFieldTokens() + 1.0f);
        }

        @Override
        public String getName() {
            return null;
        }
    }

    public static interface CollectionModel {
        public float computeProbability(BasicStats var1);

        public String getName();
    }

    public static class LMStats
    extends BasicStats {
        private float collectionProbability;

        public LMStats(String field, float queryBoost) {
            super(field, queryBoost);
        }

        public final float getCollectionProbability() {
            return this.collectionProbability;
        }

        public final void setCollectionProbability(float collectionProbability) {
            this.collectionProbability = collectionProbability;
        }
    }
}

