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
package com.atlassian.confluence.impl.search.v2.lucene.score;

import com.atlassian.confluence.impl.search.v2.lucene.WrappingQuery;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunction;
import com.atlassian.confluence.impl.search.v2.lucene.score.LuceneScoreFunctionFactory;
import com.atlassian.confluence.search.v2.query.FunctionScoreQuery;
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

public class LuceneFunctionScoreQuery
extends WrappingQuery {
    private final FunctionScoreQuery.BoostMode boostMode;
    private final LuceneScoreFunctionFactory scoreFunctionFactory;

    public LuceneFunctionScoreQuery(Query wrappedQuery, FunctionScoreQuery.BoostMode boostMode, LuceneScoreFunctionFactory scoreFunctionFactory) {
        super(wrappedQuery);
        this.boostMode = boostMode;
        this.scoreFunctionFactory = scoreFunctionFactory;
    }

    public Weight createWeight(IndexSearcher searcher) throws IOException {
        return new BoostingQueryWeight(searcher);
    }

    public String toString(String field) {
        return this.wrappedQuery.toString();
    }

    public void extractTerms(Set<Term> terms) {
        this.wrappedQuery.extractTerms(terms);
    }

    public Query rewrite(IndexReader reader) throws IOException {
        Query rewrittenContextQuery = this.wrappedQuery.rewrite(reader);
        if (rewrittenContextQuery == this.wrappedQuery) {
            return this;
        }
        return new LuceneFunctionScoreQuery(rewrittenContextQuery, this.boostMode, this.scoreFunctionFactory);
    }

    private static class EmptyScorer
    extends Scorer {
        EmptyScorer(Weight weight) {
            super(weight);
        }

        public int freq() throws IOException {
            return 0;
        }

        public float score() throws IOException {
            return 0.0f;
        }

        public int nextDoc() throws IOException {
            return Integer.MAX_VALUE;
        }

        public int docID() {
            return -1;
        }

        public int advance(int target) throws IOException {
            return Integer.MAX_VALUE;
        }

        public long cost() {
            return 0L;
        }
    }

    private static class BoostingQueryScorer
    extends Scorer {
        private final Scorer delegate;
        private final FunctionScoreQuery.BoostMode boostMode;
        private final LuceneScoreFunction scoreFunction;

        public BoostingQueryScorer(Weight weight, Scorer delegate, FunctionScoreQuery.BoostMode boostMode, LuceneScoreFunction scoreFunction) {
            super(weight);
            this.delegate = delegate;
            this.boostMode = boostMode;
            this.scoreFunction = scoreFunction;
        }

        public float score() throws IOException {
            return (float)this.boostMode.apply(this.delegate.score(), this.scoreFunction.apply(this.delegate.docID()));
        }

        public int freq() throws IOException {
            return this.delegate.freq();
        }

        public int docID() {
            return this.delegate.docID();
        }

        public int nextDoc() throws IOException {
            return this.delegate.nextDoc();
        }

        public int advance(int target) throws IOException {
            return this.delegate.advance(target);
        }

        public long cost() {
            return this.delegate.cost();
        }
    }

    private class BoostingQueryWeight
    extends Weight {
        private final Weight weight;

        private BoostingQueryWeight(IndexSearcher searcher) throws IOException {
            this.weight = LuceneFunctionScoreQuery.this.wrappedQuery.createWeight(searcher);
        }

        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            Explanation wrappedExplanation = this.weight.explain(context, doc);
            Scorer boostingScorer = this.scorer(context, true, false, context.reader().getLiveDocs());
            if (!(boostingScorer instanceof BoostingQueryScorer)) {
                return wrappedExplanation;
            }
            int newDoc = boostingScorer.advance(doc);
            if (newDoc == doc) {
                LuceneScoreFunction scoreFunction = LuceneFunctionScoreQuery.this.scoreFunctionFactory.create(context.reader());
                Explanation explanation = new Explanation();
                explanation.setValue((float)LuceneFunctionScoreQuery.this.boostMode.apply(wrappedExplanation.getValue(), scoreFunction.apply(doc)));
                explanation.setDescription("(MATCH) " + LuceneFunctionScoreQuery.this.boostMode.toString() + " of:");
                Explanation scoreFunctionExplanation = new Explanation();
                scoreFunctionExplanation.setValue((float)scoreFunction.apply(doc));
                scoreFunctionExplanation.setDescription(scoreFunction.toString());
                explanation.addDetail(scoreFunctionExplanation);
                explanation.addDetail(wrappedExplanation);
                return explanation;
            }
            return new ComplexExplanation(false, 0.0f, "no matching term");
        }

        public Query getQuery() {
            return LuceneFunctionScoreQuery.this;
        }

        public float getValueForNormalization() throws IOException {
            return this.weight.getValueForNormalization();
        }

        public void normalize(float norm, float topLevelBoost) {
            this.weight.normalize(norm, topLevelBoost *= LuceneFunctionScoreQuery.this.getBoost());
        }

        public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
            Scorer contextScorer = this.weight.scorer(context, scoreDocsInOrder, false, acceptDocs);
            if (contextScorer != null) {
                LuceneScoreFunction scoreFunction = LuceneFunctionScoreQuery.this.scoreFunctionFactory.create(context.reader());
                return new BoostingQueryScorer(this, contextScorer, LuceneFunctionScoreQuery.this.boostMode, scoreFunction);
            }
            return new EmptyScorer(this);
        }
    }
}

