/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;

public class MultiSimilarity
extends Similarity {
    protected final Similarity[] sims;

    public MultiSimilarity(Similarity[] sims) {
        this.sims = sims;
    }

    @Override
    public long computeNorm(FieldInvertState state) {
        return this.sims[0].computeNorm(state);
    }

    @Override
    public Similarity.SimWeight computeWeight(float queryBoost, CollectionStatistics collectionStats, TermStatistics ... termStats) {
        Similarity.SimWeight[] subStats = new Similarity.SimWeight[this.sims.length];
        for (int i = 0; i < subStats.length; ++i) {
            subStats[i] = this.sims[i].computeWeight(queryBoost, collectionStats, termStats);
        }
        return new MultiStats(subStats);
    }

    @Override
    public Similarity.SimScorer simScorer(Similarity.SimWeight stats, AtomicReaderContext context) throws IOException {
        Similarity.SimScorer[] subScorers = new Similarity.SimScorer[this.sims.length];
        for (int i = 0; i < subScorers.length; ++i) {
            subScorers[i] = this.sims[i].simScorer(((MultiStats)stats).subStats[i], context);
        }
        return new MultiSimScorer(subScorers);
    }

    static class MultiStats
    extends Similarity.SimWeight {
        final Similarity.SimWeight[] subStats;

        MultiStats(Similarity.SimWeight[] subStats) {
            this.subStats = subStats;
        }

        @Override
        public float getValueForNormalization() {
            float sum = 0.0f;
            for (Similarity.SimWeight stat : this.subStats) {
                sum += stat.getValueForNormalization();
            }
            return sum / (float)this.subStats.length;
        }

        @Override
        public void normalize(float queryNorm, float topLevelBoost) {
            for (Similarity.SimWeight stat : this.subStats) {
                stat.normalize(queryNorm, topLevelBoost);
            }
        }
    }

    static class MultiSimScorer
    extends Similarity.SimScorer {
        private final Similarity.SimScorer[] subScorers;

        MultiSimScorer(Similarity.SimScorer[] subScorers) {
            this.subScorers = subScorers;
        }

        @Override
        public float score(int doc, float freq) {
            float sum = 0.0f;
            for (Similarity.SimScorer subScorer : this.subScorers) {
                sum += subScorer.score(doc, freq);
            }
            return sum;
        }

        @Override
        public Explanation explain(int doc, Explanation freq) {
            Explanation expl = new Explanation(this.score(doc, freq.getValue()), "sum of:");
            for (Similarity.SimScorer subScorer : this.subScorers) {
                expl.addDetail(subScorer.explain(doc, freq));
            }
            return expl;
        }

        @Override
        public float computeSlopFactor(int distance) {
            return this.subScorers[0].computeSlopFactor(distance);
        }

        @Override
        public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {
            return this.subScorers[0].computePayloadFactor(doc, start, end, payload);
        }
    }
}

