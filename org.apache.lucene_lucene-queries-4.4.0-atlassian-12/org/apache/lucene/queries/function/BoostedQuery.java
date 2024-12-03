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
package org.apache.lucene.queries.function;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

public class BoostedQuery
extends Query {
    private Query q;
    private final ValueSource boostVal;

    public BoostedQuery(Query subQuery, ValueSource boostVal) {
        this.q = subQuery;
        this.boostVal = boostVal;
    }

    public Query getQuery() {
        return this.q;
    }

    public ValueSource getValueSource() {
        return this.boostVal;
    }

    public Query rewrite(IndexReader reader) throws IOException {
        Query newQ = this.q.rewrite(reader);
        if (newQ == this.q) {
            return this;
        }
        BoostedQuery bq = (BoostedQuery)this.clone();
        bq.q = newQ;
        return bq;
    }

    public void extractTerms(Set<Term> terms) {
        this.q.extractTerms(terms);
    }

    public Weight createWeight(IndexSearcher searcher) throws IOException {
        return new BoostedWeight(searcher);
    }

    public String toString(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("boost(").append(this.q.toString(field)).append(',').append(this.boostVal).append(')');
        sb.append(ToStringUtils.boost((float)this.getBoost()));
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        BoostedQuery other = (BoostedQuery)((Object)o);
        return this.q.equals((Object)other.q) && this.boostVal.equals(other.boostVal);
    }

    public int hashCode() {
        int h = this.q.hashCode();
        h ^= h << 17 | h >>> 16;
        h += this.boostVal.hashCode();
        h ^= h << 8 | h >>> 25;
        return h += Float.floatToIntBits(this.getBoost());
    }

    private class CustomScorer
    extends Scorer {
        private final BoostedWeight weight;
        private final float qWeight;
        private final Scorer scorer;
        private final FunctionValues vals;
        private final AtomicReaderContext readerContext;

        private CustomScorer(AtomicReaderContext readerContext, BoostedWeight w, float qWeight, Scorer scorer, ValueSource vs) throws IOException {
            super((Weight)w);
            this.weight = w;
            this.qWeight = qWeight;
            this.scorer = scorer;
            this.readerContext = readerContext;
            this.vals = vs.getValues(this.weight.fcontext, readerContext);
        }

        public int docID() {
            return this.scorer.docID();
        }

        public int advance(int target) throws IOException {
            return this.scorer.advance(target);
        }

        public int nextDoc() throws IOException {
            return this.scorer.nextDoc();
        }

        public float score() throws IOException {
            float score = this.qWeight * this.scorer.score() * this.vals.floatVal(this.scorer.docID());
            return score > Float.NEGATIVE_INFINITY ? score : -3.4028235E38f;
        }

        public int freq() throws IOException {
            return this.scorer.freq();
        }

        public Collection<Scorer.ChildScorer> getChildren() {
            return Collections.singleton(new Scorer.ChildScorer(this.scorer, "CUSTOM"));
        }

        public Explanation explain(int doc) throws IOException {
            Explanation subQueryExpl = this.weight.qWeight.explain(this.readerContext, doc);
            if (!subQueryExpl.isMatch()) {
                return subQueryExpl;
            }
            float sc = subQueryExpl.getValue() * this.vals.floatVal(doc);
            ComplexExplanation res = new ComplexExplanation(true, sc, BoostedQuery.this.toString() + ", product of:");
            res.addDetail(subQueryExpl);
            res.addDetail(this.vals.explain(doc));
            return res;
        }

        public long cost() {
            return this.scorer.cost();
        }
    }

    private class BoostedWeight
    extends Weight {
        final IndexSearcher searcher;
        Weight qWeight;
        Map fcontext;

        public BoostedWeight(IndexSearcher searcher) throws IOException {
            this.searcher = searcher;
            this.qWeight = BoostedQuery.this.q.createWeight(searcher);
            this.fcontext = ValueSource.newContext(searcher);
            BoostedQuery.this.boostVal.createWeight(this.fcontext, searcher);
        }

        public Query getQuery() {
            return BoostedQuery.this;
        }

        public float getValueForNormalization() throws IOException {
            float sum = this.qWeight.getValueForNormalization();
            return sum *= BoostedQuery.this.getBoost() * BoostedQuery.this.getBoost();
        }

        public void normalize(float norm, float topLevelBoost) {
            this.qWeight.normalize(norm, topLevelBoost *= BoostedQuery.this.getBoost());
        }

        public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
            Scorer subQueryScorer = this.qWeight.scorer(context, true, false, acceptDocs);
            if (subQueryScorer == null) {
                return null;
            }
            return new CustomScorer(context, this, BoostedQuery.this.getBoost(), subQueryScorer, BoostedQuery.this.boostVal);
        }

        public Explanation explain(AtomicReaderContext readerContext, int doc) throws IOException {
            Explanation subQueryExpl = this.qWeight.explain(readerContext, doc);
            if (!subQueryExpl.isMatch()) {
                return subQueryExpl;
            }
            FunctionValues vals = BoostedQuery.this.boostVal.getValues(this.fcontext, readerContext);
            float sc = subQueryExpl.getValue() * vals.floatVal(doc);
            ComplexExplanation res = new ComplexExplanation(true, sc, BoostedQuery.this.toString() + ", product of:");
            res.addDetail(subQueryExpl);
            res.addDetail(vals.explain(doc));
            return res;
        }
    }
}

