/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

public class MatchAllDocsQuery
extends Query {
    @Override
    public Weight createWeight(IndexSearcher searcher) {
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
        private float queryWeight;
        private float queryNorm;

        public MatchAllDocsWeight(IndexSearcher searcher) {
        }

        public String toString() {
            return "weight(" + MatchAllDocsQuery.this + ")";
        }

        @Override
        public Query getQuery() {
            return MatchAllDocsQuery.this;
        }

        @Override
        public float getValueForNormalization() {
            this.queryWeight = MatchAllDocsQuery.this.getBoost();
            return this.queryWeight * this.queryWeight;
        }

        @Override
        public void normalize(float queryNorm, float topLevelBoost) {
            this.queryNorm = queryNorm * topLevelBoost;
            this.queryWeight *= this.queryNorm;
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
            return new MatchAllScorer(context.reader(), acceptDocs, this, this.queryWeight);
        }

        @Override
        public Explanation explain(AtomicReaderContext context, int doc) {
            ComplexExplanation queryExpl = new ComplexExplanation(true, this.queryWeight, "MatchAllDocsQuery, product of:");
            if (MatchAllDocsQuery.this.getBoost() != 1.0f) {
                queryExpl.addDetail(new Explanation(MatchAllDocsQuery.this.getBoost(), "boost"));
            }
            queryExpl.addDetail(new Explanation(this.queryNorm, "queryNorm"));
            return queryExpl;
        }
    }

    private class MatchAllScorer
    extends Scorer {
        final float score;
        private int doc;
        private final int maxDoc;
        private final Bits liveDocs;

        MatchAllScorer(IndexReader reader, Bits liveDocs, Weight w, float score) {
            super(w);
            this.doc = -1;
            this.liveDocs = liveDocs;
            this.score = score;
            this.maxDoc = reader.maxDoc();
        }

        @Override
        public int docID() {
            return this.doc;
        }

        @Override
        public int nextDoc() throws IOException {
            ++this.doc;
            while (this.liveDocs != null && this.doc < this.maxDoc && !this.liveDocs.get(this.doc)) {
                ++this.doc;
            }
            if (this.doc == this.maxDoc) {
                this.doc = Integer.MAX_VALUE;
            }
            return this.doc;
        }

        @Override
        public float score() {
            return this.score;
        }

        @Override
        public int freq() {
            return 1;
        }

        @Override
        public int advance(int target) throws IOException {
            this.doc = target - 1;
            return this.nextDoc();
        }

        @Override
        public long cost() {
            return this.maxDoc;
        }
    }
}

