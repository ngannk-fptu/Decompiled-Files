/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

public class ConstantScoreQuery
extends Query {
    protected final Filter filter;
    protected final Query query;

    public ConstantScoreQuery(Query query) {
        if (query == null) {
            throw new NullPointerException("Query may not be null");
        }
        this.filter = null;
        this.query = query;
    }

    public ConstantScoreQuery(Filter filter) {
        if (filter == null) {
            throw new NullPointerException("Filter may not be null");
        }
        this.filter = filter;
        this.query = null;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public Query getQuery() {
        return this.query;
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        Query rewritten;
        if (this.query != null && (rewritten = this.query.rewrite(reader)) != this.query) {
            rewritten = new ConstantScoreQuery(rewritten);
            rewritten.setBoost(this.getBoost());
            return rewritten;
        }
        return this;
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        if (this.query != null) {
            this.query.extractTerms(terms);
        }
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
        return new ConstantWeight(searcher);
    }

    @Override
    public String toString(String field) {
        return "ConstantScore(" + (this.query == null ? this.filter.toString() : this.query.toString(field)) + ')' + ToStringUtils.boost(this.getBoost());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (o instanceof ConstantScoreQuery) {
            ConstantScoreQuery other = (ConstantScoreQuery)o;
            return (this.filter == null ? other.filter == null : this.filter.equals(other.filter)) && (this.query == null ? other.query == null : this.query.equals(other.query));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + (this.query == null ? this.filter : this.query).hashCode();
    }

    protected class ConstantScorer
    extends Scorer {
        final DocIdSetIterator docIdSetIterator;
        final float theScore;

        public ConstantScorer(DocIdSetIterator docIdSetIterator, Weight w, float theScore) {
            super(w);
            this.theScore = theScore;
            this.docIdSetIterator = docIdSetIterator;
        }

        @Override
        public int nextDoc() throws IOException {
            return this.docIdSetIterator.nextDoc();
        }

        @Override
        public int docID() {
            return this.docIdSetIterator.docID();
        }

        @Override
        public float score() throws IOException {
            assert (this.docIdSetIterator.docID() != Integer.MAX_VALUE);
            return this.theScore;
        }

        @Override
        public int freq() throws IOException {
            return 1;
        }

        @Override
        public int advance(int target) throws IOException {
            return this.docIdSetIterator.advance(target);
        }

        @Override
        public long cost() {
            return this.docIdSetIterator.cost();
        }

        private Collector wrapCollector(final Collector collector) {
            return new Collector(){

                @Override
                public void setScorer(Scorer scorer) throws IOException {
                    collector.setScorer(new ConstantScorer(scorer, ConstantScorer.this.weight, ConstantScorer.this.theScore));
                }

                @Override
                public void collect(int doc) throws IOException {
                    collector.collect(doc);
                }

                @Override
                public void setNextReader(AtomicReaderContext context) throws IOException {
                    collector.setNextReader(context);
                }

                @Override
                public boolean acceptsDocsOutOfOrder() {
                    return collector.acceptsDocsOutOfOrder();
                }
            };
        }

        @Override
        public void score(Collector collector) throws IOException {
            if (this.docIdSetIterator instanceof Scorer) {
                ((Scorer)this.docIdSetIterator).score(this.wrapCollector(collector));
            } else {
                super.score(collector);
            }
        }

        @Override
        public boolean score(Collector collector, int max, int firstDocID) throws IOException {
            if (this.docIdSetIterator instanceof Scorer) {
                return ((Scorer)this.docIdSetIterator).score(this.wrapCollector(collector), max, firstDocID);
            }
            return super.score(collector, max, firstDocID);
        }

        @Override
        public Collection<Scorer.ChildScorer> getChildren() {
            if (this.docIdSetIterator instanceof Scorer) {
                return Collections.singletonList(new Scorer.ChildScorer((Scorer)this.docIdSetIterator, "constant"));
            }
            return Collections.emptyList();
        }
    }

    protected class ConstantWeight
    extends Weight {
        private final Weight innerWeight;
        private float queryNorm;
        private float queryWeight;

        public ConstantWeight(IndexSearcher searcher) throws IOException {
            this.innerWeight = ConstantScoreQuery.this.query == null ? null : ConstantScoreQuery.this.query.createWeight(searcher);
        }

        @Override
        public Query getQuery() {
            return ConstantScoreQuery.this;
        }

        @Override
        public float getValueForNormalization() throws IOException {
            if (this.innerWeight != null) {
                this.innerWeight.getValueForNormalization();
            }
            this.queryWeight = ConstantScoreQuery.this.getBoost();
            return this.queryWeight * this.queryWeight;
        }

        @Override
        public void normalize(float norm, float topLevelBoost) {
            this.queryNorm = norm * topLevelBoost;
            this.queryWeight *= this.queryNorm;
            if (this.innerWeight != null) {
                this.innerWeight.normalize(norm, topLevelBoost);
            }
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
            DocIdSetIterator disi;
            if (ConstantScoreQuery.this.filter != null) {
                assert (ConstantScoreQuery.this.query == null);
                DocIdSet dis = ConstantScoreQuery.this.filter.getDocIdSet(context, acceptDocs);
                if (dis == null) {
                    return null;
                }
                disi = dis.iterator();
            } else {
                assert (ConstantScoreQuery.this.query != null && this.innerWeight != null);
                disi = this.innerWeight.scorer(context, scoreDocsInOrder, topScorer, acceptDocs);
            }
            if (disi == null) {
                return null;
            }
            return new ConstantScorer(disi, this, this.queryWeight);
        }

        @Override
        public boolean scoresDocsOutOfOrder() {
            return this.innerWeight != null ? this.innerWeight.scoresDocsOutOfOrder() : false;
        }

        @Override
        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            Scorer cs = this.scorer(context, true, false, context.reader().getLiveDocs());
            boolean exists = cs != null && cs.advance(doc) == doc;
            ComplexExplanation result = new ComplexExplanation();
            if (exists) {
                result.setDescription(ConstantScoreQuery.this.toString() + ", product of:");
                result.setValue(this.queryWeight);
                result.setMatch(Boolean.TRUE);
                result.addDetail(new Explanation(ConstantScoreQuery.this.getBoost(), "boost"));
                result.addDetail(new Explanation(this.queryNorm, "queryNorm"));
            } else {
                result.setDescription(ConstantScoreQuery.this.toString() + " doesn't match id " + doc);
                result.setValue(0.0f);
                result.setMatch(Boolean.FALSE);
            }
            return result;
        }
    }
}

