/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.ComplexExplanation;
import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.DocIdSetIterator;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Filter;
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
    public Weight createWeight(Searcher searcher) throws IOException {
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

        public ConstantScorer(Similarity similarity, DocIdSetIterator docIdSetIterator, Weight w) throws IOException {
            super(similarity, w);
            this.theScore = w.getValue();
            this.docIdSetIterator = docIdSetIterator;
        }

        public int nextDoc() throws IOException {
            return this.docIdSetIterator.nextDoc();
        }

        public int docID() {
            return this.docIdSetIterator.docID();
        }

        public float score() throws IOException {
            assert (this.docIdSetIterator.docID() != Integer.MAX_VALUE);
            return this.theScore;
        }

        public int advance(int target) throws IOException {
            return this.docIdSetIterator.advance(target);
        }

        private Collector wrapCollector(final Collector collector) {
            return new Collector(){

                public void setScorer(Scorer scorer) throws IOException {
                    collector.setScorer(new ConstantScorer(ConstantScorer.this.getSimilarity(), scorer, ConstantScorer.this.weight));
                }

                public void collect(int doc) throws IOException {
                    collector.collect(doc);
                }

                public void setNextReader(IndexReader reader, int docBase) throws IOException {
                    collector.setNextReader(reader, docBase);
                }

                public boolean acceptsDocsOutOfOrder() {
                    return collector.acceptsDocsOutOfOrder();
                }
            };
        }

        public void score(Collector collector) throws IOException {
            if (this.docIdSetIterator instanceof Scorer) {
                ((Scorer)this.docIdSetIterator).score(this.wrapCollector(collector));
            } else {
                super.score(collector);
            }
        }

        protected boolean score(Collector collector, int max, int firstDocID) throws IOException {
            if (this.docIdSetIterator instanceof Scorer) {
                return ((Scorer)this.docIdSetIterator).score(this.wrapCollector(collector), max, firstDocID);
            }
            return super.score(collector, max, firstDocID);
        }
    }

    protected class ConstantWeight
    extends Weight {
        private final Weight innerWeight;
        private final Similarity similarity;
        private float queryNorm;
        private float queryWeight;

        public ConstantWeight(Searcher searcher) throws IOException {
            this.similarity = ConstantScoreQuery.this.getSimilarity(searcher);
            this.innerWeight = ConstantScoreQuery.this.query == null ? null : ConstantScoreQuery.this.query.createWeight(searcher);
        }

        public Query getQuery() {
            return ConstantScoreQuery.this;
        }

        public float getValue() {
            return this.queryWeight;
        }

        public float sumOfSquaredWeights() throws IOException {
            if (this.innerWeight != null) {
                this.innerWeight.sumOfSquaredWeights();
            }
            this.queryWeight = ConstantScoreQuery.this.getBoost();
            return this.queryWeight * this.queryWeight;
        }

        public void normalize(float norm) {
            this.queryNorm = norm;
            this.queryWeight *= this.queryNorm;
            if (this.innerWeight != null) {
                this.innerWeight.normalize(norm);
            }
        }

        public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
            DocIdSetIterator disi;
            if (ConstantScoreQuery.this.filter != null) {
                assert (ConstantScoreQuery.this.query == null);
                DocIdSet dis = ConstantScoreQuery.this.filter.getDocIdSet(reader);
                if (dis == null) {
                    return null;
                }
                disi = dis.iterator();
            } else {
                assert (ConstantScoreQuery.this.query != null && this.innerWeight != null);
                disi = this.innerWeight.scorer(reader, scoreDocsInOrder, topScorer);
            }
            if (disi == null) {
                return null;
            }
            return new ConstantScorer(this.similarity, disi, this);
        }

        public boolean scoresDocsOutOfOrder() {
            return this.innerWeight != null ? this.innerWeight.scoresDocsOutOfOrder() : false;
        }

        public Explanation explain(IndexReader reader, int doc) throws IOException {
            Scorer cs = this.scorer(reader, true, false);
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

