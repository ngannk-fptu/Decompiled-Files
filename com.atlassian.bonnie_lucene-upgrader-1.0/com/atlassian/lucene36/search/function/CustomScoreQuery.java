/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.ComplexExplanation;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.search.function.CustomScoreProvider;
import com.atlassian.lucene36.search.function.ValueSourceQuery;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CustomScoreQuery
extends Query {
    private Query subQuery;
    private ValueSourceQuery[] valSrcQueries;
    private boolean strict;

    public CustomScoreQuery(Query subQuery) {
        this(subQuery, new ValueSourceQuery[0]);
    }

    public CustomScoreQuery(Query subQuery, ValueSourceQuery valSrcQuery) {
        ValueSourceQuery[] valueSourceQueryArray;
        if (valSrcQuery != null) {
            ValueSourceQuery[] valueSourceQueryArray2 = new ValueSourceQuery[1];
            valueSourceQueryArray = valueSourceQueryArray2;
            valueSourceQueryArray2[0] = valSrcQuery;
        } else {
            valueSourceQueryArray = new ValueSourceQuery[]{};
        }
        this(subQuery, valueSourceQueryArray);
    }

    public CustomScoreQuery(Query subQuery, ValueSourceQuery ... valSrcQueries) {
        this.strict = false;
        this.subQuery = subQuery;
        ValueSourceQuery[] valueSourceQueryArray = this.valSrcQueries = valSrcQueries != null ? valSrcQueries : new ValueSourceQuery[]{};
        if (subQuery == null) {
            throw new IllegalArgumentException("<subquery> must not be null!");
        }
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        CustomScoreQuery clone = null;
        Query sq = this.subQuery.rewrite(reader);
        if (sq != this.subQuery) {
            clone = (CustomScoreQuery)this.clone();
            clone.subQuery = sq;
        }
        for (int i = 0; i < this.valSrcQueries.length; ++i) {
            ValueSourceQuery v = (ValueSourceQuery)this.valSrcQueries[i].rewrite(reader);
            if (v == this.valSrcQueries[i]) continue;
            if (clone == null) {
                clone = (CustomScoreQuery)this.clone();
            }
            clone.valSrcQueries[i] = v;
        }
        return clone == null ? this : clone;
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        this.subQuery.extractTerms(terms);
        for (int i = 0; i < this.valSrcQueries.length; ++i) {
            this.valSrcQueries[i].extractTerms(terms);
        }
    }

    @Override
    public Object clone() {
        CustomScoreQuery clone = (CustomScoreQuery)super.clone();
        clone.subQuery = (Query)this.subQuery.clone();
        clone.valSrcQueries = new ValueSourceQuery[this.valSrcQueries.length];
        for (int i = 0; i < this.valSrcQueries.length; ++i) {
            clone.valSrcQueries[i] = (ValueSourceQuery)this.valSrcQueries[i].clone();
        }
        return clone;
    }

    @Override
    public String toString(String field) {
        StringBuilder sb = new StringBuilder(this.name()).append("(");
        sb.append(this.subQuery.toString(field));
        for (int i = 0; i < this.valSrcQueries.length; ++i) {
            sb.append(", ").append(this.valSrcQueries[i].toString(field));
        }
        sb.append(")");
        sb.append(this.strict ? " STRICT" : "");
        return sb.toString() + ToStringUtils.boost(this.getBoost());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        CustomScoreQuery other = (CustomScoreQuery)o;
        if (this.getBoost() != other.getBoost() || !this.subQuery.equals(other.subQuery) || this.strict != other.strict || this.valSrcQueries.length != other.valSrcQueries.length) {
            return false;
        }
        return Arrays.equals(this.valSrcQueries, other.valSrcQueries);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.subQuery.hashCode() + Arrays.hashCode(this.valSrcQueries) ^ Float.floatToIntBits(this.getBoost()) ^ (this.strict ? 1234 : 4321);
    }

    protected CustomScoreProvider getCustomScoreProvider(IndexReader reader) throws IOException {
        return new CustomScoreProvider(reader);
    }

    @Override
    public Weight createWeight(Searcher searcher) throws IOException {
        return new CustomWeight(searcher);
    }

    public boolean isStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public String name() {
        return "custom";
    }

    private class CustomScorer
    extends Scorer {
        private final float qWeight;
        private Scorer subQueryScorer;
        private Scorer[] valSrcScorers;
        private final CustomScoreProvider provider;
        private float[] vScores;

        private CustomScorer(Similarity similarity, IndexReader reader, CustomWeight w, Scorer subQueryScorer, Scorer[] valSrcScorers) throws IOException {
            super(similarity, w);
            this.qWeight = w.getValue();
            this.subQueryScorer = subQueryScorer;
            this.valSrcScorers = valSrcScorers;
            this.vScores = new float[valSrcScorers.length];
            this.provider = CustomScoreQuery.this.getCustomScoreProvider(reader);
        }

        public int nextDoc() throws IOException {
            int doc = this.subQueryScorer.nextDoc();
            if (doc != Integer.MAX_VALUE) {
                for (int i = 0; i < this.valSrcScorers.length; ++i) {
                    this.valSrcScorers[i].advance(doc);
                }
            }
            return doc;
        }

        public int docID() {
            return this.subQueryScorer.docID();
        }

        public float score() throws IOException {
            for (int i = 0; i < this.valSrcScorers.length; ++i) {
                this.vScores[i] = this.valSrcScorers[i].score();
            }
            return this.qWeight * this.provider.customScore(this.subQueryScorer.docID(), this.subQueryScorer.score(), this.vScores);
        }

        public int advance(int target) throws IOException {
            int doc = this.subQueryScorer.advance(target);
            if (doc != Integer.MAX_VALUE) {
                for (int i = 0; i < this.valSrcScorers.length; ++i) {
                    this.valSrcScorers[i].advance(doc);
                }
            }
            return doc;
        }
    }

    private class CustomWeight
    extends Weight {
        Similarity similarity;
        Weight subQueryWeight;
        Weight[] valSrcWeights;
        boolean qStrict;

        public CustomWeight(Searcher searcher) throws IOException {
            this.similarity = CustomScoreQuery.this.getSimilarity(searcher);
            this.subQueryWeight = CustomScoreQuery.this.subQuery.createWeight(searcher);
            this.valSrcWeights = new Weight[CustomScoreQuery.this.valSrcQueries.length];
            for (int i = 0; i < CustomScoreQuery.this.valSrcQueries.length; ++i) {
                this.valSrcWeights[i] = CustomScoreQuery.this.valSrcQueries[i].createWeight(searcher);
            }
            this.qStrict = CustomScoreQuery.this.strict;
        }

        public Query getQuery() {
            return CustomScoreQuery.this;
        }

        public float getValue() {
            return CustomScoreQuery.this.getBoost();
        }

        public float sumOfSquaredWeights() throws IOException {
            float sum = this.subQueryWeight.sumOfSquaredWeights();
            for (int i = 0; i < this.valSrcWeights.length; ++i) {
                if (this.qStrict) {
                    this.valSrcWeights[i].sumOfSquaredWeights();
                    continue;
                }
                sum += this.valSrcWeights[i].sumOfSquaredWeights();
            }
            return sum *= CustomScoreQuery.this.getBoost() * CustomScoreQuery.this.getBoost();
        }

        public void normalize(float norm) {
            this.subQueryWeight.normalize(norm *= CustomScoreQuery.this.getBoost());
            for (int i = 0; i < this.valSrcWeights.length; ++i) {
                if (this.qStrict) {
                    this.valSrcWeights[i].normalize(1.0f);
                    continue;
                }
                this.valSrcWeights[i].normalize(norm);
            }
        }

        public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
            Scorer subQueryScorer = this.subQueryWeight.scorer(reader, true, false);
            if (subQueryScorer == null) {
                return null;
            }
            Scorer[] valSrcScorers = new Scorer[this.valSrcWeights.length];
            for (int i = 0; i < valSrcScorers.length; ++i) {
                valSrcScorers[i] = this.valSrcWeights[i].scorer(reader, true, topScorer);
            }
            return new CustomScorer(this.similarity, reader, this, subQueryScorer, valSrcScorers);
        }

        public Explanation explain(IndexReader reader, int doc) throws IOException {
            Explanation explain = this.doExplain(reader, doc);
            return explain == null ? new Explanation(0.0f, "no matching docs") : explain;
        }

        private Explanation doExplain(IndexReader reader, int doc) throws IOException {
            Explanation subQueryExpl = this.subQueryWeight.explain(reader, doc);
            if (!subQueryExpl.isMatch()) {
                return subQueryExpl;
            }
            Explanation[] valSrcExpls = new Explanation[this.valSrcWeights.length];
            for (int i = 0; i < this.valSrcWeights.length; ++i) {
                valSrcExpls[i] = this.valSrcWeights[i].explain(reader, doc);
            }
            Explanation customExp = CustomScoreQuery.this.getCustomScoreProvider(reader).customExplain(doc, subQueryExpl, valSrcExpls);
            float sc = this.getValue() * customExp.getValue();
            ComplexExplanation res = new ComplexExplanation(true, sc, CustomScoreQuery.this.toString() + ", product of:");
            res.addDetail(customExp);
            res.addDetail(new Explanation(this.getValue(), "queryBoost"));
            return res;
        }

        public boolean scoresDocsOutOfOrder() {
            return false;
        }
    }
}

