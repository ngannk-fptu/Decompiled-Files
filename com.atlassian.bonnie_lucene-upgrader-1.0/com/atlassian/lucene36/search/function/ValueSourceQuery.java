/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.search.ComplexExplanation;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.search.function.DocValues;
import com.atlassian.lucene36.search.function.ValueSource;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ValueSourceQuery
extends Query {
    ValueSource valSrc;

    public ValueSourceQuery(ValueSource valSrc) {
        this.valSrc = valSrc;
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        return this;
    }

    @Override
    public void extractTerms(Set<Term> terms) {
    }

    @Override
    public Weight createWeight(Searcher searcher) {
        return new ValueSourceWeight(searcher);
    }

    @Override
    public String toString(String field) {
        return this.valSrc.toString() + ToStringUtils.boost(this.getBoost());
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
        ValueSourceQuery other = (ValueSourceQuery)o;
        return this.getBoost() == other.getBoost() && this.valSrc.equals(other.valSrc);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.valSrc.hashCode() ^ Float.floatToIntBits(this.getBoost());
    }

    private class ValueSourceScorer
    extends Scorer {
        private final float qWeight;
        private final DocValues vals;
        private final TermDocs termDocs;
        private int doc;

        private ValueSourceScorer(Similarity similarity, IndexReader reader, ValueSourceWeight w) throws IOException {
            super(similarity, w);
            this.doc = -1;
            this.qWeight = w.getValue();
            this.vals = ValueSourceQuery.this.valSrc.getValues(reader);
            this.termDocs = reader.termDocs(null);
        }

        public int nextDoc() throws IOException {
            this.doc = this.termDocs.next() ? this.termDocs.doc() : Integer.MAX_VALUE;
            return this.doc;
        }

        public int docID() {
            return this.doc;
        }

        public int advance(int target) throws IOException {
            this.doc = this.termDocs.skipTo(target) ? this.termDocs.doc() : Integer.MAX_VALUE;
            return this.doc;
        }

        public float score() throws IOException {
            return this.qWeight * this.vals.floatVal(this.termDocs.doc());
        }
    }

    class ValueSourceWeight
    extends Weight {
        Similarity similarity;
        float queryNorm;
        float queryWeight;

        public ValueSourceWeight(Searcher searcher) {
            this.similarity = ValueSourceQuery.this.getSimilarity(searcher);
        }

        public Query getQuery() {
            return ValueSourceQuery.this;
        }

        public float getValue() {
            return this.queryWeight;
        }

        public float sumOfSquaredWeights() throws IOException {
            this.queryWeight = ValueSourceQuery.this.getBoost();
            return this.queryWeight * this.queryWeight;
        }

        public void normalize(float norm) {
            this.queryNorm = norm;
            this.queryWeight *= this.queryNorm;
        }

        public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
            return new ValueSourceScorer(this.similarity, reader, this);
        }

        public Explanation explain(IndexReader reader, int doc) throws IOException {
            DocValues vals = ValueSourceQuery.this.valSrc.getValues(reader);
            float sc = this.queryWeight * vals.floatVal(doc);
            ComplexExplanation result = new ComplexExplanation(true, sc, ValueSourceQuery.this.toString() + ", product of:");
            result.addDetail(vals.explain(doc));
            result.addDetail(new Explanation(ValueSourceQuery.this.getBoost(), "boost"));
            result.addDetail(new Explanation(this.queryNorm, "queryNorm"));
            return result;
        }
    }
}

