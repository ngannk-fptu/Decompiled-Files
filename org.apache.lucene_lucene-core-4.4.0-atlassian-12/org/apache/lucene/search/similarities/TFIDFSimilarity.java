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

public abstract class TFIDFSimilarity
extends Similarity {
    @Override
    public abstract float coord(int var1, int var2);

    @Override
    public abstract float queryNorm(float var1);

    public abstract float tf(float var1);

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

    public abstract float idf(long var1, long var3);

    public abstract float lengthNorm(FieldInvertState var1);

    @Override
    public final long computeNorm(FieldInvertState state) {
        float normValue = this.lengthNorm(state);
        return this.encodeNormValue(normValue);
    }

    public abstract float decodeNormValue(long var1);

    public abstract long encodeNormValue(float var1);

    public abstract float sloppyFreq(int var1);

    public abstract float scorePayload(int var1, int var2, int var3, BytesRef var4);

    @Override
    public final Similarity.SimWeight computeWeight(float queryBoost, CollectionStatistics collectionStats, TermStatistics ... termStats) {
        Explanation idf = termStats.length == 1 ? this.idfExplain(collectionStats, termStats[0]) : this.idfExplain(collectionStats, termStats);
        return new IDFStats(collectionStats.field(), idf, queryBoost);
    }

    @Override
    public final Similarity.SimScorer simScorer(Similarity.SimWeight stats, AtomicReaderContext context) throws IOException {
        IDFStats idfstats = (IDFStats)stats;
        return new TFIDFSimScorer(idfstats, context.reader().getNormValues(idfstats.field));
    }

    private Explanation explainScore(int doc, Explanation freq, IDFStats stats, NumericDocValues norms) {
        Explanation result = new Explanation();
        result.setDescription("score(doc=" + doc + ",freq=" + freq + "), product of:");
        Explanation queryExpl = new Explanation();
        queryExpl.setDescription("queryWeight, product of:");
        Explanation boostExpl = new Explanation(stats.queryBoost, "boost");
        if (stats.queryBoost != 1.0f) {
            queryExpl.addDetail(boostExpl);
        }
        queryExpl.addDetail(stats.idf);
        Explanation queryNormExpl = new Explanation(stats.queryNorm, "queryNorm");
        queryExpl.addDetail(queryNormExpl);
        queryExpl.setValue(boostExpl.getValue() * stats.idf.getValue() * queryNormExpl.getValue());
        result.addDetail(queryExpl);
        Explanation fieldExpl = new Explanation();
        fieldExpl.setDescription("fieldWeight in " + doc + ", product of:");
        Explanation tfExplanation = new Explanation();
        tfExplanation.setValue(this.tf(freq.getValue()));
        tfExplanation.setDescription("tf(freq=" + freq.getValue() + "), with freq of:");
        tfExplanation.addDetail(freq);
        fieldExpl.addDetail(tfExplanation);
        fieldExpl.addDetail(stats.idf);
        Explanation fieldNormExpl = new Explanation();
        float fieldNorm = norms != null ? this.decodeNormValue(norms.get(doc)) : 1.0f;
        fieldNormExpl.setValue(fieldNorm);
        fieldNormExpl.setDescription("fieldNorm(doc=" + doc + ")");
        fieldExpl.addDetail(fieldNormExpl);
        fieldExpl.setValue(tfExplanation.getValue() * stats.idf.getValue() * fieldNormExpl.getValue());
        result.addDetail(fieldExpl);
        result.setValue(queryExpl.getValue() * fieldExpl.getValue());
        if (queryExpl.getValue() == 1.0f) {
            return fieldExpl;
        }
        return result;
    }

    private static class IDFStats
    extends Similarity.SimWeight {
        private final String field;
        private final Explanation idf;
        private float queryNorm;
        private float queryWeight;
        private final float queryBoost;
        private float value;

        public IDFStats(String field, Explanation idf, float queryBoost) {
            this.field = field;
            this.idf = idf;
            this.queryBoost = queryBoost;
            this.queryWeight = idf.getValue() * queryBoost;
        }

        @Override
        public float getValueForNormalization() {
            return this.queryWeight * this.queryWeight;
        }

        @Override
        public void normalize(float queryNorm, float topLevelBoost) {
            this.queryNorm = queryNorm * topLevelBoost;
            this.queryWeight *= this.queryNorm;
            this.value = this.queryWeight * this.idf.getValue();
        }
    }

    private final class TFIDFSimScorer
    extends Similarity.SimScorer {
        private final IDFStats stats;
        private final float weightValue;
        private final NumericDocValues norms;

        TFIDFSimScorer(IDFStats stats, NumericDocValues norms) throws IOException {
            this.stats = stats;
            this.weightValue = stats.value;
            this.norms = norms;
        }

        @Override
        public float score(int doc, float freq) {
            float raw = TFIDFSimilarity.this.tf(freq) * this.weightValue;
            return this.norms == null ? raw : raw * TFIDFSimilarity.this.decodeNormValue(this.norms.get(doc));
        }

        @Override
        public float computeSlopFactor(int distance) {
            return TFIDFSimilarity.this.sloppyFreq(distance);
        }

        @Override
        public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {
            return TFIDFSimilarity.this.scorePayload(doc, start, end, payload);
        }

        @Override
        public Explanation explain(int doc, Explanation freq) {
            return TFIDFSimilarity.this.explainScore(doc, freq, this.stats, this.norms);
        }
    }
}

