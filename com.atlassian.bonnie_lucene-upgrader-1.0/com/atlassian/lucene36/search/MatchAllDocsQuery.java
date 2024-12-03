/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

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
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MatchAllDocsQuery
extends Query {
    private final String normsField;

    public MatchAllDocsQuery() {
        this(null);
    }

    public MatchAllDocsQuery(String normsField) {
        this.normsField = normsField;
    }

    @Override
    public Weight createWeight(Searcher searcher) {
        return new MatchAllDocsWeight(searcher);
    }

    @Override
    public void extractTerms(Set<Term> terms) {
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("*:*");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MatchAllDocsQuery)) {
            return false;
        }
        MatchAllDocsQuery other = (MatchAllDocsQuery)o;
        return this.getBoost() == other.getBoost();
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.getBoost()) ^ 0x1AA71190;
    }

    private class MatchAllDocsWeight
    extends Weight {
        private Similarity similarity;
        private float queryWeight;
        private float queryNorm;

        public MatchAllDocsWeight(Searcher searcher) {
            this.similarity = searcher.getSimilarity();
        }

        public String toString() {
            return "weight(" + MatchAllDocsQuery.this + ")";
        }

        public Query getQuery() {
            return MatchAllDocsQuery.this;
        }

        public float getValue() {
            return this.queryWeight;
        }

        public float sumOfSquaredWeights() {
            this.queryWeight = MatchAllDocsQuery.this.getBoost();
            return this.queryWeight * this.queryWeight;
        }

        public void normalize(float queryNorm) {
            this.queryNorm = queryNorm;
            this.queryWeight *= this.queryNorm;
        }

        public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
            return new MatchAllScorer(reader, this.similarity, this, MatchAllDocsQuery.this.normsField != null ? reader.norms(MatchAllDocsQuery.this.normsField) : null);
        }

        public Explanation explain(IndexReader reader, int doc) {
            ComplexExplanation queryExpl = new ComplexExplanation(true, this.getValue(), "MatchAllDocsQuery, product of:");
            if (MatchAllDocsQuery.this.getBoost() != 1.0f) {
                queryExpl.addDetail(new Explanation(MatchAllDocsQuery.this.getBoost(), "boost"));
            }
            queryExpl.addDetail(new Explanation(this.queryNorm, "queryNorm"));
            return queryExpl;
        }
    }

    private class MatchAllScorer
    extends Scorer {
        final TermDocs termDocs;
        final float score;
        final byte[] norms;
        private int doc;

        MatchAllScorer(IndexReader reader, Similarity similarity, Weight w, byte[] norms) throws IOException {
            super(similarity, w);
            this.doc = -1;
            this.termDocs = reader.termDocs(null);
            this.score = w.getValue();
            this.norms = norms;
        }

        public int docID() {
            return this.doc;
        }

        public int nextDoc() throws IOException {
            this.doc = this.termDocs.next() ? this.termDocs.doc() : Integer.MAX_VALUE;
            return this.doc;
        }

        public float score() {
            return this.norms == null ? this.score : this.score * this.getSimilarity().decodeNormValue(this.norms[this.docID()]);
        }

        public int advance(int target) throws IOException {
            this.doc = this.termDocs.skipTo(target) ? this.termDocs.doc() : Integer.MAX_VALUE;
            return this.doc;
        }
    }
}

