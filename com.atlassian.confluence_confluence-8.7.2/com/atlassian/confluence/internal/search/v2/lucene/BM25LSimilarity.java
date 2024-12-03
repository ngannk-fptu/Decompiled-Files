/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.FieldInvertState
 *  org.apache.lucene.index.NumericDocValues
 *  org.apache.lucene.search.CollectionStatistics
 *  org.apache.lucene.search.Explanation
 *  org.apache.lucene.search.TermStatistics
 *  org.apache.lucene.search.similarities.Similarity
 *  org.apache.lucene.search.similarities.Similarity$SimScorer
 *  org.apache.lucene.search.similarities.Similarity$SimWeight
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.SmallFloat
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.SmallFloat;

public class BM25LSimilarity
extends Similarity {
    private final float k1;
    private final float b;
    private final float d;
    protected boolean discountOverlaps = true;
    private static final float[] NORM_TABLE = new float[256];

    public BM25LSimilarity(float k1, float b, float d) {
        this.k1 = k1;
        this.b = b;
        this.d = d;
    }

    public BM25LSimilarity() {
        this.k1 = 1.25f;
        this.b = 0.4f;
        this.d = 0.5f;
    }

    protected float idf(long docFreq, long numDocs) {
        return (float)Math.log(1.0 + ((double)(numDocs - docFreq) + 0.5) / ((double)docFreq + 0.5));
    }

    protected float sloppyFreq(int distance) {
        return 1.0f / (float)(distance + 1);
    }

    protected float scorePayload(int doc, int start, int end, BytesRef payload) {
        return 1.0f;
    }

    protected float avgFieldLength(CollectionStatistics collectionStats) {
        long sumTotalTermFreq = collectionStats.sumTotalTermFreq();
        if (sumTotalTermFreq <= 0L) {
            return 1.0f;
        }
        return (float)((double)sumTotalTermFreq / (double)collectionStats.maxDoc());
    }

    protected byte encodeNormValue(float boost, int fieldLength) {
        return SmallFloat.floatToByte315((float)(boost / (float)Math.sqrt(fieldLength)));
    }

    protected float decodeNormValue(byte b) {
        return NORM_TABLE[b & 0xFF];
    }

    public void setDiscountOverlaps(boolean v) {
        this.discountOverlaps = v;
    }

    public boolean getDiscountOverlaps() {
        return this.discountOverlaps;
    }

    public final long computeNorm(FieldInvertState state) {
        int numTerms = this.discountOverlaps ? state.getLength() - state.getNumOverlap() : state.getLength();
        return this.encodeNormValue(state.getBoost(), numTerms);
    }

    public Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics termStats) {
        long df = termStats.docFreq();
        long max = collectionStats.maxDoc();
        float idf = this.idf(df, max);
        return new Explanation(idf, "idf(docFreq=" + df + ", maxDocs=" + max + ")");
    }

    public Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics[] termStats) {
        long max = collectionStats.maxDoc();
        float idf = 0.0f;
        Explanation exp = new Explanation();
        exp.setDescription("idf(), sum of:");
        for (TermStatistics stat : termStats) {
            long df = stat.docFreq();
            float termIdf = this.idf(df, max);
            exp.addDetail(new Explanation(termIdf, "idf(docFreq=" + df + ", maxDocs=" + max + ")"));
            idf += termIdf;
        }
        exp.setValue(idf);
        return exp;
    }

    public final Similarity.SimWeight computeWeight(float queryBoost, CollectionStatistics collectionStats, TermStatistics ... termStats) {
        Explanation idf = termStats.length == 1 ? this.idfExplain(collectionStats, termStats[0]) : this.idfExplain(collectionStats, termStats);
        float avgdl = this.avgFieldLength(collectionStats);
        float[] cache = new float[256];
        for (int i = 0; i < cache.length; ++i) {
            cache[i] = 1.0f - this.b + this.b * this.decodeNormValue((byte)i) / avgdl;
        }
        return new BM25LStats(collectionStats.field(), idf, queryBoost, avgdl, cache);
    }

    public final Similarity.SimScorer simScorer(Similarity.SimWeight stats, AtomicReaderContext context) throws IOException {
        BM25LStats bm25stats = (BM25LStats)stats;
        return new BM25DocScorer(bm25stats, context.reader().getNormValues(bm25stats.field));
    }

    private Explanation explainScore(int doc, Explanation freq, BM25LStats stats, NumericDocValues norms) {
        Explanation result = new Explanation();
        result.setDescription("score(doc=" + doc + ",freq=" + freq + "), product of:");
        Explanation boostExpl = new Explanation(stats.queryBoost * stats.topLevelBoost, "boost");
        if (boostExpl.getValue() != 1.0f) {
            result.addDetail(boostExpl);
        }
        result.addDetail(stats.idf);
        Explanation tfNormExpl = new Explanation();
        tfNormExpl.setDescription("tfNorm, computed from:");
        tfNormExpl.addDetail(freq);
        tfNormExpl.addDetail(new Explanation(this.k1, "parameter k1"));
        if (norms == null) {
            tfNormExpl.addDetail(new Explanation(0.0f, "parameter b (norms omitted for field)"));
            tfNormExpl.setValue(freq.getValue() * (this.k1 + 1.0f) / (freq.getValue() + this.k1));
        } else {
            float doclen = this.decodeNormValue((byte)norms.get(doc));
            tfNormExpl.addDetail(new Explanation(this.b, "parameter b"));
            tfNormExpl.addDetail(new Explanation(stats.avgdl, "avgFieldLength"));
            tfNormExpl.addDetail(new Explanation(doclen, "fieldLength"));
            float cPrime = freq.getValue() / (1.0f - this.b + this.b * doclen / stats.avgdl);
            if (cPrime > 0.0f) {
                tfNormExpl.setValue((this.k1 + 1.0f) * (cPrime + this.d) / (this.k1 + (cPrime + this.d)));
            } else {
                tfNormExpl.setValue(0.0f);
            }
        }
        result.addDetail(tfNormExpl);
        result.setValue(boostExpl.getValue() * stats.idf.getValue() * tfNormExpl.getValue());
        return result;
    }

    public String toString() {
        return "BM25L(k1=" + this.k1 + ",b=" + this.b + ",d=" + this.d + ")";
    }

    public float getK1() {
        return this.k1;
    }

    public float getB() {
        return this.b;
    }

    public float getDelta() {
        return this.d;
    }

    static {
        for (int i = 0; i < 256; ++i) {
            float f = SmallFloat.byte315ToFloat((byte)((byte)i));
            BM25LSimilarity.NORM_TABLE[i] = 1.0f / (f * f);
        }
    }

    private static class BM25LStats
    extends Similarity.SimWeight {
        private final Explanation idf;
        private final float avgdl;
        private final float queryBoost;
        private float topLevelBoost;
        private float weight;
        private final String field;
        private final float[] cache;

        BM25LStats(String field, Explanation idf, float queryBoost, float avgdl, float[] cache) {
            this.field = field;
            this.idf = idf;
            this.queryBoost = queryBoost;
            this.avgdl = avgdl;
            this.cache = cache;
        }

        public float getValueForNormalization() {
            float queryWeight = this.idf.getValue() * this.queryBoost;
            return queryWeight * queryWeight;
        }

        public void normalize(float queryNorm, float topLevelBoost) {
            this.topLevelBoost = topLevelBoost;
            this.weight = this.idf.getValue() * this.queryBoost * topLevelBoost;
        }
    }

    private class BM25DocScorer
    extends Similarity.SimScorer {
        private final BM25LStats stats;
        private final float weightValue;
        private final NumericDocValues norms;
        private final float[] cache;

        BM25DocScorer(BM25LStats stats, NumericDocValues norms) throws IOException {
            this.stats = stats;
            this.weightValue = stats.weight * (BM25LSimilarity.this.k1 + 1.0f);
            this.cache = stats.cache;
            this.norms = norms;
        }

        public float score(int doc, float freq) {
            float norm = this.norms == null ? BM25LSimilarity.this.k1 : this.cache[(byte)this.norms.get(doc) & 0xFF];
            float cPrime = freq / norm;
            if (cPrime > 0.0f) {
                return this.weightValue * (cPrime + BM25LSimilarity.this.d) / (BM25LSimilarity.this.k1 + (cPrime + BM25LSimilarity.this.d));
            }
            return 0.0f;
        }

        public Explanation explain(int doc, Explanation freq) {
            return BM25LSimilarity.this.explainScore(doc, freq, this.stats, this.norms);
        }

        public float computeSlopFactor(int distance) {
            return BM25LSimilarity.this.sloppyFreq(distance);
        }

        public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {
            return BM25LSimilarity.this.scorePayload(doc, start, end, payload);
        }
    }
}

