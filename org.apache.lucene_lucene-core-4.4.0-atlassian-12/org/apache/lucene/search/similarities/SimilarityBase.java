/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.similarities;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.MultiSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.SmallFloat;

public abstract class SimilarityBase
extends Similarity {
    private static final double LOG_2 = Math.log(2.0);
    protected boolean discountOverlaps = true;
    private static final float[] NORM_TABLE = new float[256];

    public void setDiscountOverlaps(boolean v) {
        this.discountOverlaps = v;
    }

    public boolean getDiscountOverlaps() {
        return this.discountOverlaps;
    }

    @Override
    public final Similarity.SimWeight computeWeight(float queryBoost, CollectionStatistics collectionStats, TermStatistics ... termStats) {
        Similarity.SimWeight[] stats = new BasicStats[termStats.length];
        for (int i = 0; i < termStats.length; ++i) {
            stats[i] = this.newStats(collectionStats.field(), queryBoost);
            this.fillBasicStats((BasicStats)stats[i], collectionStats, termStats[i]);
        }
        return stats.length == 1 ? stats[0] : new MultiSimilarity.MultiStats(stats);
    }

    protected BasicStats newStats(String field, float queryBoost) {
        return new BasicStats(field, queryBoost);
    }

    protected void fillBasicStats(BasicStats stats, CollectionStatistics collectionStats, TermStatistics termStats) {
        float avgFieldLength;
        long numberOfFieldTokens;
        long sumTotalTermFreq;
        assert (collectionStats.sumTotalTermFreq() == -1L || collectionStats.sumTotalTermFreq() >= termStats.totalTermFreq());
        long numberOfDocuments = collectionStats.maxDoc();
        long docFreq = termStats.docFreq();
        long totalTermFreq = termStats.totalTermFreq();
        if (totalTermFreq == -1L) {
            totalTermFreq = docFreq;
        }
        if ((sumTotalTermFreq = collectionStats.sumTotalTermFreq()) <= 0L) {
            numberOfFieldTokens = docFreq;
            avgFieldLength = 1.0f;
        } else {
            numberOfFieldTokens = sumTotalTermFreq;
            avgFieldLength = (float)numberOfFieldTokens / (float)numberOfDocuments;
        }
        stats.setNumberOfDocuments(numberOfDocuments);
        stats.setNumberOfFieldTokens(numberOfFieldTokens);
        stats.setAvgFieldLength(avgFieldLength);
        stats.setDocFreq(docFreq);
        stats.setTotalTermFreq(totalTermFreq);
    }

    protected abstract float score(BasicStats var1, float var2, float var3);

    protected void explain(Explanation expl, BasicStats stats, int doc, float freq, float docLen) {
    }

    protected Explanation explain(BasicStats stats, int doc, Explanation freq, float docLen) {
        Explanation result = new Explanation();
        result.setValue(this.score(stats, freq.getValue(), docLen));
        result.setDescription("score(" + this.getClass().getSimpleName() + ", doc=" + doc + ", freq=" + freq.getValue() + "), computed from:");
        result.addDetail(freq);
        this.explain(result, stats, doc, freq.getValue(), docLen);
        return result;
    }

    @Override
    public Similarity.SimScorer simScorer(Similarity.SimWeight stats, AtomicReaderContext context) throws IOException {
        if (stats instanceof MultiSimilarity.MultiStats) {
            Similarity.SimWeight[] subStats = ((MultiSimilarity.MultiStats)stats).subStats;
            Similarity.SimScorer[] subScorers = new Similarity.SimScorer[subStats.length];
            for (int i = 0; i < subScorers.length; ++i) {
                BasicStats basicstats = (BasicStats)subStats[i];
                subScorers[i] = new BasicSimScorer(basicstats, context.reader().getNormValues(basicstats.field));
            }
            return new MultiSimilarity.MultiSimScorer(subScorers);
        }
        BasicStats basicstats = (BasicStats)stats;
        return new BasicSimScorer(basicstats, context.reader().getNormValues(basicstats.field));
    }

    public abstract String toString();

    @Override
    public long computeNorm(FieldInvertState state) {
        float numTerms = this.discountOverlaps ? (float)(state.getLength() - state.getNumOverlap()) : (float)state.getLength() / state.getBoost();
        return this.encodeNormValue(state.getBoost(), numTerms);
    }

    protected float decodeNormValue(byte norm) {
        return NORM_TABLE[norm & 0xFF];
    }

    protected byte encodeNormValue(float boost, float length) {
        return SmallFloat.floatToByte315(boost / (float)Math.sqrt(length));
    }

    public static double log2(double x) {
        return Math.log(x) / LOG_2;
    }

    static {
        for (int i = 0; i < 256; ++i) {
            float floatNorm = SmallFloat.byte315ToFloat((byte)i);
            SimilarityBase.NORM_TABLE[i] = 1.0f / (floatNorm * floatNorm);
        }
    }

    private class BasicSimScorer
    extends Similarity.SimScorer {
        private final BasicStats stats;
        private final NumericDocValues norms;

        BasicSimScorer(BasicStats stats, NumericDocValues norms) throws IOException {
            this.stats = stats;
            this.norms = norms;
        }

        @Override
        public float score(int doc, float freq) {
            return SimilarityBase.this.score(this.stats, freq, this.norms == null ? 1.0f : SimilarityBase.this.decodeNormValue((byte)this.norms.get(doc)));
        }

        @Override
        public Explanation explain(int doc, Explanation freq) {
            return SimilarityBase.this.explain(this.stats, doc, freq, this.norms == null ? 1.0f : SimilarityBase.this.decodeNormValue((byte)this.norms.get(doc)));
        }

        @Override
        public float computeSlopFactor(int distance) {
            return 1.0f / (float)(distance + 1);
        }

        @Override
        public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {
            return 1.0f;
        }
    }
}

