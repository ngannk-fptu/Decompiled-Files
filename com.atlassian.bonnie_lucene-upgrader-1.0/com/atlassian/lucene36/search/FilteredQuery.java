/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Collector;
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
public class FilteredQuery
extends Query {
    Query query;
    Filter filter;

    public FilteredQuery(Query query, Filter filter) {
        this.query = query;
        this.filter = filter;
    }

    @Override
    public Weight createWeight(Searcher searcher) throws IOException {
        final Weight weight = this.query.createWeight(searcher);
        final Similarity similarity = this.query.getSimilarity(searcher);
        return new Weight(){
            private float value;

            public float getValue() {
                return this.value;
            }

            public boolean scoresDocsOutOfOrder() {
                return false;
            }

            public float sumOfSquaredWeights() throws IOException {
                return weight.sumOfSquaredWeights() * FilteredQuery.this.getBoost() * FilteredQuery.this.getBoost();
            }

            public void normalize(float v) {
                weight.normalize(v * FilteredQuery.this.getBoost());
                this.value = weight.getValue();
            }

            public Explanation explain(IndexReader ir, int i) throws IOException {
                DocIdSetIterator docIdSetIterator;
                Explanation inner = weight.explain(ir, i);
                Filter f = FilteredQuery.this.filter;
                DocIdSet docIdSet = f.getDocIdSet(ir);
                DocIdSetIterator docIdSetIterator2 = docIdSetIterator = docIdSet == null ? DocIdSet.EMPTY_DOCIDSET.iterator() : docIdSet.iterator();
                if (docIdSetIterator == null) {
                    docIdSetIterator = DocIdSet.EMPTY_DOCIDSET.iterator();
                }
                if (docIdSetIterator.advance(i) == i) {
                    return inner;
                }
                Explanation result = new Explanation(0.0f, "failure to match filter: " + f.toString());
                result.addDetail(inner);
                return result;
            }

            public Query getQuery() {
                return FilteredQuery.this;
            }

            public Scorer scorer(IndexReader indexReader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
                return FilteredQuery.getFilteredScorer(indexReader, similarity, weight, this, FilteredQuery.this.filter);
            }
        };
    }

    static Scorer getFilteredScorer(IndexReader indexReader, Similarity similarity, Weight weight, Weight wrapperWeight, Filter filter) throws IOException {
        assert (filter != null);
        DocIdSet filterDocIdSet = filter.getDocIdSet(indexReader);
        if (filterDocIdSet == null) {
            return null;
        }
        final DocIdSetIterator filterIter = filterDocIdSet.iterator();
        if (filterIter == null) {
            return null;
        }
        final Scorer scorer = weight.scorer(indexReader, true, false);
        return scorer == null ? null : new Scorer(similarity, wrapperWeight){
            private int scorerDoc;
            private int filterDoc;
            {
                super(x0, x1);
                this.scorerDoc = -1;
                this.filterDoc = -1;
            }

            public void score(Collector collector) throws IOException {
                int filterDoc = filterIter.nextDoc();
                int scorerDoc = scorer.advance(filterDoc);
                collector.setScorer(scorer);
                while (true) {
                    if (scorerDoc == filterDoc) {
                        if (scorerDoc == Integer.MAX_VALUE) break;
                        collector.collect(scorerDoc);
                        filterDoc = filterIter.nextDoc();
                        scorerDoc = scorer.advance(filterDoc);
                        continue;
                    }
                    if (scorerDoc > filterDoc) {
                        filterDoc = filterIter.advance(scorerDoc);
                        continue;
                    }
                    scorerDoc = scorer.advance(filterDoc);
                }
            }

            private int advanceToNextCommonDoc() throws IOException {
                while (true) {
                    if (this.scorerDoc < this.filterDoc) {
                        this.scorerDoc = scorer.advance(this.filterDoc);
                        continue;
                    }
                    if (this.scorerDoc == this.filterDoc) {
                        return this.scorerDoc;
                    }
                    this.filterDoc = filterIter.advance(this.scorerDoc);
                }
            }

            public int nextDoc() throws IOException {
                this.filterDoc = filterIter.nextDoc();
                return this.advanceToNextCommonDoc();
            }

            public int advance(int target) throws IOException {
                if (target > this.filterDoc) {
                    this.filterDoc = filterIter.advance(target);
                }
                return this.advanceToNextCommonDoc();
            }

            public int docID() {
                return this.scorerDoc;
            }

            public float score() throws IOException {
                return scorer.score();
            }
        };
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        Query rewritten = this.query.rewrite(reader);
        if (rewritten != this.query) {
            FilteredQuery clone = (FilteredQuery)this.clone();
            clone.query = rewritten;
            return clone;
        }
        return this;
    }

    public Query getQuery() {
        return this.query;
    }

    public Filter getFilter() {
        return this.filter;
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        this.getQuery().extractTerms(terms);
    }

    @Override
    public String toString(String s) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("filtered(");
        buffer.append(this.query.toString(s));
        buffer.append(")->");
        buffer.append(this.filter);
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FilteredQuery) {
            FilteredQuery fq = (FilteredQuery)o;
            return this.query.equals(fq.query) && this.filter.equals(fq.filter) && this.getBoost() == fq.getBoost();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.query.hashCode() ^ this.filter.hashCode() + Float.floatToRawIntBits(this.getBoost());
    }
}

