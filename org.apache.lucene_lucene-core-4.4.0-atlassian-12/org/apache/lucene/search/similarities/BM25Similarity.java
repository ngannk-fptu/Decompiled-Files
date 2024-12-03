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
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.SmallFloat;

public class BM25Similarity
extends Similarity {
    private final float k1;
    private final float b;
    protected boolean discountOverlaps = true;
    private static final float[] NORM_TABLE = new float[256];

    public BM25Similarity(float k1, float b) {
        this.k1 = k1;
        this.b = b;
    }

    public BM25Similarity() {
        this.k1 = 1.2f;
        this.b = 0.75f;
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
        return SmallFloat.floatToByte315(boost / (float)Math.sqrt(fieldLength));
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

    @Override
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

    @Override
    public final Similarity.SimWeight computeWeight(float queryBoost, CollectionStatistics collectionStats, TermStatistics ... termStats) {
        Explanation idf = termStats.length == 1 ? this.idfExplain(collectionStats, termStats[0]) : this.idfExplain(collectionStats, termStats);
        float avgdl = this.avgFieldLength(collectionStats);
        float[] cache = new float[256];
        for (int i = 0; i < cache.length; ++i) {
            cache[i] = this.k1 * (1.0f - this.b + this.b * this.decodeNormValue((byte)i) / avgdl);
        }
        return new BM25Stats(collectionStats.field(), idf, queryBoost, avgdl, cache);
    }

    @Override
    public final Similarity.SimScorer simScorer(Similarity.SimWeight stats, AtomicReaderContext context) throws IOException {
        BM25Stats bm25stats = (BM25Stats)stats;
        return new BM25DocScorer(bm25stats, context.reader().getNormValues(bm25stats.field));
    }

    private Explanation explainScore(int doc, Explanation freq, BM25Stats stats, NumericDocValues norms) {
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
            tfNormExpl.setValue(freq.getValue() * (this.k1 + 1.0f) / (freq.getValue() + this.k1 * (1.0f - this.b + this.b * doclen / stats.avgdl)));
        }
        result.addDetail(tfNormExpl);
        result.setValue(boostExpl.getValue() * stats.idf.getValue() * tfNormExpl.getValue());
        return result;
    }

    public String toString() {
        return "BM25(k1=" + this.k1 + ",b=" + this.b + ")";
    }

    public float getK1() {
        return this.k1;
    }

    public float getB() {
        return this.b;
    }

    static {
        for (int i = 0; i < 256; ++i) {
            float f = SmallFloat.byte315ToFloat((byte)i);
            BM25Similarity.NORM_TABLE[i] = 1.0f / (f * f);
        }
    }

    private static class BM25Stats
    extends Similarity.SimWeight {
        private final Explanation idf;
        private final float avgdl;
        private final float queryBoost;
        private float topLevelBoost;
        private float weight;
        private final String field;
        private final float[] cache;

        BM25Stats(String field, Explanation idf, float queryBoost, float avgdl, float[] cache) {
            this.field = field;
            this.idf = idf;
            this.queryBoost = queryBoost;
            this.avgdl = avgdl;
            this.cache = cache;
        }

        @Override
        public float getValueForNormalization() {
            float queryWeight = this.idf.getValue() * this.queryBoost;
            return queryWeight * queryWeight;
        }

        @Override
        public void normalize(float queryNorm, float topLevelBoost) {
            this.topLevelBoost = topLevelBoost;
            this.weight = this.idf.getValue() * this.queryBoost * topLevelBoost;
        }
    }

    private class BM25DocScorer
    extends Similarity.SimScorer {
        private final BM25Stats stats;
        private final float weightValue;
        private final NumericDocValues norms;
        private final float[] cache;

        BM25DocScorer(BM25Stats stats, NumericDocValues norms) throws IOException {
            this.stats = stats;
            this.weightValue = stats.weight * (BM25Similarity.this.k1 + 1.0f);
            this.cache = stats.cache;
            this.norms = norms;
        }

        @Override
        public float score(int doc, float freq) {
            float norm = this.norms == null ? BM25Similarity.this.k1 : this.cache[(byte)this.norms.get(doc) & 0xFF];
            return this.weightValue * freq / (freq + norm);
        }

        @Override
        public Explanation explain(int doc, Explanation freq) {
            return BM25Similarity.this.explainScore(doc, freq, this.stats, this.norms);
        }

        @Override
        public float computeSlopFactor(int distance) {
            return BM25Similarity.this.sloppyFreq(distance);
        }

        @Override
        public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {
            return BM25Similarity.this.scorePayload(doc, start, end, payload);
        }
    }
}

