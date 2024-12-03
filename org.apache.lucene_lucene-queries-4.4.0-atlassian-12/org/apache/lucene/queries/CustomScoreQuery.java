/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.ComplexExplanation
 *  org.apache.lucene.search.Explanation
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.Scorer
 *  org.apache.lucene.search.Scorer$ChildScorer
 *  org.apache.lucene.search.Weight
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.ToStringUtils
 */
package org.apache.lucene.queries;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

public class CustomScoreQuery
extends Query {
    private Query subQuery;
    private Query[] scoringQueries;
    private boolean strict;

    public CustomScoreQuery(Query subQuery) {
        this(subQuery, new FunctionQuery[0]);
    }

    public CustomScoreQuery(Query subQuery, FunctionQuery scoringQuery) {
        FunctionQuery[] functionQueryArray;
        if (scoringQuery != null) {
            FunctionQuery[] functionQueryArray2 = new FunctionQuery[1];
            functionQueryArray = functionQueryArray2;
            functionQueryArray2[0] = scoringQuery;
        } else {
            functionQueryArray = new FunctionQuery[]{};
        }
        this(subQuery, functionQueryArray);
    }

    public CustomScoreQuery(Query subQuery, FunctionQuery ... scoringQueries) {
        this.strict = false;
        this.subQuery = subQuery;
        FunctionQuery[] functionQueryArray = this.scoringQueries = scoringQueries != null ? scoringQueries : new Query[]{};
        if (subQuery == null) {
            throw new IllegalArgumentException("<subquery> must not be null!");
        }
    }

    public Query rewrite(IndexReader reader) throws IOException {
        CustomScoreQuery clone = null;
        Query sq = this.subQuery.rewrite(reader);
        if (sq != this.subQuery) {
            clone = this.clone();
            clone.subQuery = sq;
        }
        for (int i = 0; i < this.scoringQueries.length; ++i) {
            Query v = this.scoringQueries[i].rewrite(reader);
            if (v == this.scoringQueries[i]) continue;
            if (clone == null) {
                clone = this.clone();
            }
            clone.scoringQueries[i] = v;
        }
        return clone == null ? this : clone;
    }

    public void extractTerms(Set<Term> terms) {
        this.subQuery.extractTerms(terms);
        for (Query scoringQuery : this.scoringQueries) {
            scoringQuery.extractTerms(terms);
        }
    }

    public CustomScoreQuery clone() {
        CustomScoreQuery clone = (CustomScoreQuery)super.clone();
        clone.subQuery = this.subQuery.clone();
        clone.scoringQueries = new Query[this.scoringQueries.length];
        for (int i = 0; i < this.scoringQueries.length; ++i) {
            clone.scoringQueries[i] = this.scoringQueries[i].clone();
        }
        return clone;
    }

    public String toString(String field) {
        StringBuilder sb = new StringBuilder(this.name()).append("(");
        sb.append(this.subQuery.toString(field));
        for (Query scoringQuery : this.scoringQueries) {
            sb.append(", ").append(scoringQuery.toString(field));
        }
        sb.append(")");
        sb.append(this.strict ? " STRICT" : "");
        return sb.toString() + ToStringUtils.boost((float)this.getBoost());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        CustomScoreQuery other = (CustomScoreQuery)((Object)o);
        if (this.getBoost() != other.getBoost() || !this.subQuery.equals((Object)other.subQuery) || this.strict != other.strict || this.scoringQueries.length != other.scoringQueries.length) {
            return false;
        }
        return Arrays.equals(this.scoringQueries, other.scoringQueries);
    }

    public int hashCode() {
        return ((Object)((Object)this)).getClass().hashCode() + this.subQuery.hashCode() + Arrays.hashCode(this.scoringQueries) ^ Float.floatToIntBits(this.getBoost()) ^ (this.strict ? 1234 : 4321);
    }

    protected CustomScoreProvider getCustomScoreProvider(AtomicReaderContext context) throws IOException {
        return new CustomScoreProvider(context);
    }

    public Weight createWeight(IndexSearcher searcher) throws IOException {
        return new CustomWeight(searcher);
    }

    public boolean isStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public Query getSubQuery() {
        return this.subQuery;
    }

    public Query[] getScoringQueries() {
        return this.scoringQueries;
    }

    public String name() {
        return "custom";
    }

    private class CustomScorer
    extends Scorer {
        private final float qWeight;
        private final Scorer subQueryScorer;
        private final Scorer[] valSrcScorers;
        private final CustomScoreProvider provider;
        private final float[] vScores;

        private CustomScorer(CustomScoreProvider provider, CustomWeight w, float qWeight, Scorer subQueryScorer, Scorer[] valSrcScorers) {
            super((Weight)w);
            this.qWeight = qWeight;
            this.subQueryScorer = subQueryScorer;
            this.valSrcScorers = valSrcScorers;
            this.vScores = new float[valSrcScorers.length];
            this.provider = provider;
        }

        public int nextDoc() throws IOException {
            int doc = this.subQueryScorer.nextDoc();
            if (doc != Integer.MAX_VALUE) {
                for (Scorer valSrcScorer : this.valSrcScorers) {
                    valSrcScorer.advance(doc);
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

        public int freq() throws IOException {
            return this.subQueryScorer.freq();
        }

        public Collection<Scorer.ChildScorer> getChildren() {
            return Collections.singleton(new Scorer.ChildScorer(this.subQueryScorer, "CUSTOM"));
        }

        public int advance(int target) throws IOException {
            int doc = this.subQueryScorer.advance(target);
            if (doc != Integer.MAX_VALUE) {
                for (Scorer valSrcScorer : this.valSrcScorers) {
                    valSrcScorer.advance(doc);
                }
            }
            return doc;
        }

        public long cost() {
            return this.subQueryScorer.cost();
        }
    }

    private class CustomWeight
    extends Weight {
        Weight subQueryWeight;
        Weight[] valSrcWeights;
        boolean qStrict;
        float queryWeight;

        public CustomWeight(IndexSearcher searcher) throws IOException {
            this.subQueryWeight = CustomScoreQuery.this.subQuery.createWeight(searcher);
            this.valSrcWeights = new Weight[CustomScoreQuery.this.scoringQueries.length];
            for (int i = 0; i < CustomScoreQuery.this.scoringQueries.length; ++i) {
                this.valSrcWeights[i] = CustomScoreQuery.this.scoringQueries[i].createWeight(searcher);
            }
            this.qStrict = CustomScoreQuery.this.strict;
        }

        public Query getQuery() {
            return CustomScoreQuery.this;
        }

        public float getValueForNormalization() throws IOException {
            float sum = this.subQueryWeight.getValueForNormalization();
            for (Weight valSrcWeight : this.valSrcWeights) {
                if (this.qStrict) {
                    valSrcWeight.getValueForNormalization();
                    continue;
                }
                sum += valSrcWeight.getValueForNormalization();
            }
            return sum;
        }

        public void normalize(float norm, float topLevelBoost) {
            this.subQueryWeight.normalize(norm, 1.0f);
            for (Weight valSrcWeight : this.valSrcWeights) {
                if (this.qStrict) {
                    valSrcWeight.normalize(1.0f, 1.0f);
                    continue;
                }
                valSrcWeight.normalize(norm, 1.0f);
            }
            this.queryWeight = topLevelBoost * CustomScoreQuery.this.getBoost();
        }

        public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
            Scorer subQueryScorer = this.subQueryWeight.scorer(context, true, false, acceptDocs);
            if (subQueryScorer == null) {
                return null;
            }
            Scorer[] valSrcScorers = new Scorer[this.valSrcWeights.length];
            for (int i = 0; i < valSrcScorers.length; ++i) {
                valSrcScorers[i] = this.valSrcWeights[i].scorer(context, true, topScorer, acceptDocs);
            }
            return new CustomScorer(CustomScoreQuery.this.getCustomScoreProvider(context), this, this.queryWeight, subQueryScorer, valSrcScorers);
        }

        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            Explanation explain = this.doExplain(context, doc);
            return explain == null ? new Explanation(0.0f, "no matching docs") : explain;
        }

        private Explanation doExplain(AtomicReaderContext info, int doc) throws IOException {
            Explanation subQueryExpl = this.subQueryWeight.explain(info, doc);
            if (!subQueryExpl.isMatch()) {
                return subQueryExpl;
            }
            Explanation[] valSrcExpls = new Explanation[this.valSrcWeights.length];
            for (int i = 0; i < this.valSrcWeights.length; ++i) {
                valSrcExpls[i] = this.valSrcWeights[i].explain(info, doc);
            }
            Explanation customExp = CustomScoreQuery.this.getCustomScoreProvider(info).customExplain(doc, subQueryExpl, valSrcExpls);
            float sc = CustomScoreQuery.this.getBoost() * customExp.getValue();
            ComplexExplanation res = new ComplexExplanation(true, sc, CustomScoreQuery.this.toString() + ", product of:");
            res.addDetail(customExp);
            res.addDetail(new Explanation(CustomScoreQuery.this.getBoost(), "queryBoost"));
            return res;
        }

        public boolean scoresDocsOutOfOrder() {
            return false;
        }
    }
}

