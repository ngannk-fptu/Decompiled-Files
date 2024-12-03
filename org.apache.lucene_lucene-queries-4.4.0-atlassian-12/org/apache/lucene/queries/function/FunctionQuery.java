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
 *  org.apache.lucene.search.Weight
 *  org.apache.lucene.util.Bits
 */
package org.apache.lucene.queries.function;

import java.io.IOException;
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

public class FunctionQuery
extends Query {
    final ValueSource func;

    public FunctionQuery(ValueSource func) {
        this.func = func;
    }

    public ValueSource getValueSource() {
        return this.func;
    }

    public Query rewrite(IndexReader reader) throws IOException {
        return this;
    }

    public void extractTerms(Set<Term> terms) {
    }

    public Weight createWeight(IndexSearcher searcher) throws IOException {
        return new FunctionWeight(searcher);
    }

    public String toString(String field) {
        float boost = this.getBoost();
        return ((double)boost != 1.0 ? "(" : "") + this.func.toString() + ((double)boost == 1.0 ? "" : ")^" + boost);
    }

    public boolean equals(Object o) {
        if (!FunctionQuery.class.isInstance(o)) {
            return false;
        }
        FunctionQuery other = (FunctionQuery)((Object)o);
        return this.getBoost() == other.getBoost() && this.func.equals(other.func);
    }

    public int hashCode() {
        return this.func.hashCode() * 31 + Float.floatToIntBits(this.getBoost());
    }

    protected class AllScorer
    extends Scorer {
        final IndexReader reader;
        final FunctionWeight weight;
        final int maxDoc;
        final float qWeight;
        int doc;
        final FunctionValues vals;
        final Bits acceptDocs;

        public AllScorer(AtomicReaderContext context, Bits acceptDocs, FunctionWeight w, float qWeight) throws IOException {
            super((Weight)w);
            this.doc = -1;
            this.weight = w;
            this.qWeight = qWeight;
            this.reader = context.reader();
            this.maxDoc = this.reader.maxDoc();
            this.acceptDocs = acceptDocs;
            this.vals = FunctionQuery.this.func.getValues(this.weight.context, context);
        }

        public int docID() {
            return this.doc;
        }

        public int nextDoc() throws IOException {
            do {
                ++this.doc;
                if (this.doc < this.maxDoc) continue;
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            } while (this.acceptDocs != null && !this.acceptDocs.get(this.doc));
            return this.doc;
        }

        public int advance(int target) throws IOException {
            this.doc = target - 1;
            return this.nextDoc();
        }

        public float score() throws IOException {
            float score = this.qWeight * this.vals.floatVal(this.doc);
            return score > Float.NEGATIVE_INFINITY ? score : -3.4028235E38f;
        }

        public long cost() {
            return this.maxDoc;
        }

        public int freq() throws IOException {
            return 1;
        }

        public Explanation explain(int doc) throws IOException {
            float sc = this.qWeight * this.vals.floatVal(doc);
            ComplexExplanation result = new ComplexExplanation(true, sc, "FunctionQuery(" + FunctionQuery.this.func + "), product of:");
            result.addDetail(this.vals.explain(doc));
            result.addDetail(new Explanation(FunctionQuery.this.getBoost(), "boost"));
            result.addDetail(new Explanation(this.weight.queryNorm, "queryNorm"));
            return result;
        }
    }

    protected class FunctionWeight
    extends Weight {
        protected final IndexSearcher searcher;
        protected float queryNorm;
        protected float queryWeight;
        protected final Map context;

        public FunctionWeight(IndexSearcher searcher) throws IOException {
            this.searcher = searcher;
            this.context = ValueSource.newContext(searcher);
            FunctionQuery.this.func.createWeight(this.context, searcher);
        }

        public Query getQuery() {
            return FunctionQuery.this;
        }

        public float getValueForNormalization() throws IOException {
            this.queryWeight = FunctionQuery.this.getBoost();
            return this.queryWeight * this.queryWeight;
        }

        public void normalize(float norm, float topLevelBoost) {
            this.queryNorm = norm * topLevelBoost;
            this.queryWeight *= this.queryNorm;
        }

        public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
            return new AllScorer(context, acceptDocs, this, this.queryWeight);
        }

        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            return ((AllScorer)this.scorer(context, true, true, context.reader().getLiveDocs())).explain(doc);
        }
    }
}

