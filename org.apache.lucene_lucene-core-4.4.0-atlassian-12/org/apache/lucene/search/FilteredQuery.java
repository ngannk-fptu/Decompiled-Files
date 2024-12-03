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
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

public class FilteredQuery
extends Query {
    private final Query query;
    private final Filter filter;
    private final FilterStrategy strategy;
    public static final FilterStrategy RANDOM_ACCESS_FILTER_STRATEGY = new RandomAccessFilterStrategy();
    public static final FilterStrategy LEAP_FROG_FILTER_FIRST_STRATEGY = new LeapFrogFilterStrategy(false);
    public static final FilterStrategy LEAP_FROG_QUERY_FIRST_STRATEGY = new LeapFrogFilterStrategy(true);
    public static final FilterStrategy QUERY_FIRST_FILTER_STRATEGY = new QueryFirstFilterStrategy();

    public FilteredQuery(Query query, Filter filter) {
        this(query, filter, RANDOM_ACCESS_FILTER_STRATEGY);
    }

    public FilteredQuery(Query query, Filter filter, FilterStrategy strategy) {
        if (query == null || filter == null) {
            throw new IllegalArgumentException("Query and filter cannot be null.");
        }
        if (strategy == null) {
            throw new IllegalArgumentException("FilterStrategy can not be null");
        }
        this.strategy = strategy;
        this.query = query;
        this.filter = filter;
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
        final Weight weight = this.query.createWeight(searcher);
        return new Weight(){

            @Override
            public boolean scoresDocsOutOfOrder() {
                return true;
            }

            @Override
            public float getValueForNormalization() throws IOException {
                return weight.getValueForNormalization() * FilteredQuery.this.getBoost() * FilteredQuery.this.getBoost();
            }

            @Override
            public void normalize(float norm, float topLevelBoost) {
                weight.normalize(norm, topLevelBoost * FilteredQuery.this.getBoost());
            }

            @Override
            public Explanation explain(AtomicReaderContext ir, int i) throws IOException {
                DocIdSetIterator docIdSetIterator;
                Explanation inner = weight.explain(ir, i);
                Filter f = FilteredQuery.this.filter;
                DocIdSet docIdSet = f.getDocIdSet(ir, ir.reader().getLiveDocs());
                DocIdSetIterator docIdSetIterator2 = docIdSetIterator = docIdSet == null ? DocIdSetIterator.empty() : docIdSet.iterator();
                if (docIdSetIterator == null) {
                    docIdSetIterator = DocIdSetIterator.empty();
                }
                if (docIdSetIterator.advance(i) == i) {
                    return inner;
                }
                Explanation result = new Explanation(0.0f, "failure to match filter: " + f.toString());
                result.addDetail(inner);
                return result;
            }

            @Override
            public Query getQuery() {
                return FilteredQuery.this;
            }

            @Override
            public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
                assert (FilteredQuery.this.filter != null);
                DocIdSet filterDocIdSet = FilteredQuery.this.filter.getDocIdSet(context, acceptDocs);
                if (filterDocIdSet == null) {
                    return null;
                }
                return FilteredQuery.this.strategy.filteredScorer(context, scoreDocsInOrder, topScorer, weight, filterDocIdSet);
            }
        };
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        Query queryRewritten = this.query.rewrite(reader);
        if (queryRewritten instanceof MatchAllDocsQuery) {
            ConstantScoreQuery rewritten = new ConstantScoreQuery(this.filter);
            rewritten.setBoost(this.getBoost() * queryRewritten.getBoost());
            return rewritten;
        }
        if (queryRewritten != this.query) {
            FilteredQuery rewritten = new FilteredQuery(queryRewritten, this.filter, this.strategy);
            rewritten.setBoost(this.getBoost());
            return rewritten;
        }
        return this;
    }

    public final Query getQuery() {
        return this.query;
    }

    public final Filter getFilter() {
        return this.filter;
    }

    public FilterStrategy getFilterStrategy() {
        return this.strategy;
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
        if (o == this) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        assert (o instanceof FilteredQuery);
        FilteredQuery fq = (FilteredQuery)o;
        return fq.query.equals(this.query) && fq.filter.equals(this.filter) && fq.strategy.equals(this.strategy);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + this.strategy.hashCode();
        hash = hash * 31 + this.query.hashCode();
        hash = hash * 31 + this.filter.hashCode();
        return hash;
    }

    private static final class QueryFirstFilterStrategy
    extends FilterStrategy {
        private QueryFirstFilterStrategy() {
        }

        @Override
        public Scorer filteredScorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Weight weight, DocIdSet docIdSet) throws IOException {
            Bits filterAcceptDocs = docIdSet.bits();
            if (filterAcceptDocs == null) {
                return LEAP_FROG_QUERY_FIRST_STRATEGY.filteredScorer(context, scoreDocsInOrder, topScorer, weight, docIdSet);
            }
            Scorer scorer = weight.scorer(context, true, false, null);
            return scorer == null ? null : new QueryFirstScorer(weight, filterAcceptDocs, scorer);
        }
    }

    private static final class LeapFrogFilterStrategy
    extends FilterStrategy {
        private final boolean scorerFirst;

        private LeapFrogFilterStrategy(boolean scorerFirst) {
            this.scorerFirst = scorerFirst;
        }

        @Override
        public Scorer filteredScorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Weight weight, DocIdSet docIdSet) throws IOException {
            DocIdSetIterator filterIter = docIdSet.iterator();
            if (filterIter == null) {
                return null;
            }
            Scorer scorer = weight.scorer(context, true, false, null);
            if (this.scorerFirst) {
                return scorer == null ? null : new LeapFrogScorer(weight, scorer, filterIter, scorer);
            }
            return scorer == null ? null : new LeapFrogScorer(weight, filterIter, scorer, scorer);
        }
    }

    public static class RandomAccessFilterStrategy
    extends FilterStrategy {
        @Override
        public Scorer filteredScorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Weight weight, DocIdSet docIdSet) throws IOException {
            boolean useRandomAccess;
            DocIdSetIterator filterIter = docIdSet.iterator();
            if (filterIter == null) {
                return null;
            }
            int firstFilterDoc = filterIter.nextDoc();
            if (firstFilterDoc == Integer.MAX_VALUE) {
                return null;
            }
            Bits filterAcceptDocs = docIdSet.bits();
            boolean bl = useRandomAccess = filterAcceptDocs != null && this.useRandomAccess(filterAcceptDocs, firstFilterDoc);
            if (useRandomAccess) {
                return weight.scorer(context, scoreDocsInOrder, topScorer, filterAcceptDocs);
            }
            assert (firstFilterDoc > -1);
            Scorer scorer = weight.scorer(context, true, false, null);
            return scorer == null ? null : new PrimaryAdvancedLeapFrogScorer(weight, firstFilterDoc, filterIter, scorer);
        }

        protected boolean useRandomAccess(Bits bits, int firstFilterDoc) {
            return firstFilterDoc < 100;
        }
    }

    public static abstract class FilterStrategy {
        public abstract Scorer filteredScorer(AtomicReaderContext var1, boolean var2, boolean var3, Weight var4, DocIdSet var5) throws IOException;
    }

    private static final class PrimaryAdvancedLeapFrogScorer
    extends LeapFrogScorer {
        private final int firstFilteredDoc;

        protected PrimaryAdvancedLeapFrogScorer(Weight weight, int firstFilteredDoc, DocIdSetIterator filterIter, Scorer other) {
            super(weight, filterIter, other, other);
            this.firstFilteredDoc = firstFilteredDoc;
            this.primaryDoc = firstFilteredDoc;
        }

        @Override
        protected int primaryNext() throws IOException {
            if (this.secondaryDoc != -1) {
                return super.primaryNext();
            }
            return this.firstFilteredDoc;
        }
    }

    private static class LeapFrogScorer
    extends Scorer {
        private final DocIdSetIterator secondary;
        private final DocIdSetIterator primary;
        private final Scorer scorer;
        protected int primaryDoc = -1;
        protected int secondaryDoc = -1;

        protected LeapFrogScorer(Weight weight, DocIdSetIterator primary, DocIdSetIterator secondary, Scorer scorer) {
            super(weight);
            this.primary = primary;
            this.secondary = secondary;
            this.scorer = scorer;
        }

        @Override
        public final void score(Collector collector) throws IOException {
            collector.setScorer(this.scorer);
            int primDoc = this.primaryNext();
            int secDoc = this.secondary.advance(primDoc);
            while (true) {
                if (primDoc == secDoc) {
                    if (primDoc == Integer.MAX_VALUE) break;
                    collector.collect(primDoc);
                    primDoc = this.primary.nextDoc();
                    secDoc = this.secondary.advance(primDoc);
                    continue;
                }
                if (secDoc > primDoc) {
                    primDoc = this.primary.advance(secDoc);
                    continue;
                }
                secDoc = this.secondary.advance(primDoc);
            }
        }

        private final int advanceToNextCommonDoc() throws IOException {
            while (true) {
                if (this.secondaryDoc < this.primaryDoc) {
                    this.secondaryDoc = this.secondary.advance(this.primaryDoc);
                    continue;
                }
                if (this.secondaryDoc == this.primaryDoc) {
                    return this.primaryDoc;
                }
                this.primaryDoc = this.primary.advance(this.secondaryDoc);
            }
        }

        @Override
        public final int nextDoc() throws IOException {
            this.primaryDoc = this.primaryNext();
            return this.advanceToNextCommonDoc();
        }

        protected int primaryNext() throws IOException {
            return this.primary.nextDoc();
        }

        @Override
        public final int advance(int target) throws IOException {
            if (target > this.primaryDoc) {
                this.primaryDoc = this.primary.advance(target);
            }
            return this.advanceToNextCommonDoc();
        }

        @Override
        public final int docID() {
            return this.secondaryDoc;
        }

        @Override
        public final float score() throws IOException {
            return this.scorer.score();
        }

        @Override
        public final int freq() throws IOException {
            return this.scorer.freq();
        }

        @Override
        public final Collection<Scorer.ChildScorer> getChildren() {
            return Collections.singleton(new Scorer.ChildScorer(this.scorer, "FILTERED"));
        }

        @Override
        public long cost() {
            return Math.min(this.primary.cost(), this.secondary.cost());
        }
    }

    private static final class QueryFirstScorer
    extends Scorer {
        private final Scorer scorer;
        private int scorerDoc = -1;
        private Bits filterbits;

        protected QueryFirstScorer(Weight weight, Bits filterBits, Scorer other) {
            super(weight);
            this.scorer = other;
            this.filterbits = filterBits;
        }

        @Override
        public void score(Collector collector) throws IOException {
            int scorerDoc;
            collector.setScorer(this.scorer);
            while ((scorerDoc = this.scorer.nextDoc()) != Integer.MAX_VALUE) {
                if (!this.filterbits.get(scorerDoc)) continue;
                collector.collect(scorerDoc);
            }
        }

        @Override
        public int nextDoc() throws IOException {
            int doc;
            while ((doc = this.scorer.nextDoc()) != Integer.MAX_VALUE && !this.filterbits.get(doc)) {
            }
            this.scorerDoc = doc;
            return this.scorerDoc;
        }

        @Override
        public int advance(int target) throws IOException {
            int doc = this.scorer.advance(target);
            if (doc != Integer.MAX_VALUE && !this.filterbits.get(doc)) {
                this.scorerDoc = this.nextDoc();
                return this.scorerDoc;
            }
            this.scorerDoc = doc;
            return this.scorerDoc;
        }

        @Override
        public int docID() {
            return this.scorerDoc;
        }

        @Override
        public float score() throws IOException {
            return this.scorer.score();
        }

        @Override
        public int freq() throws IOException {
            return this.scorer.freq();
        }

        @Override
        public Collection<Scorer.ChildScorer> getChildren() {
            return Collections.singleton(new Scorer.ChildScorer(this.scorer, "FILTERED"));
        }

        @Override
        public long cost() {
            return this.scorer.cost();
        }
    }
}

