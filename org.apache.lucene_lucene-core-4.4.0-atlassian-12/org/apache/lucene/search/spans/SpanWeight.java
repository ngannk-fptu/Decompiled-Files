/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanScorer;
import org.apache.lucene.util.Bits;

public class SpanWeight
extends Weight {
    protected Similarity similarity;
    protected Map<Term, TermContext> termContexts;
    protected SpanQuery query;
    protected Similarity.SimWeight stats;

    public SpanWeight(SpanQuery query, IndexSearcher searcher) throws IOException {
        this.similarity = searcher.getSimilarity();
        this.query = query;
        this.termContexts = new HashMap<Term, TermContext>();
        TreeSet<Term> terms = new TreeSet<Term>();
        query.extractTerms(terms);
        IndexReaderContext context = searcher.getTopReaderContext();
        TermStatistics[] termStats = new TermStatistics[terms.size()];
        int i = 0;
        for (Term term : terms) {
            TermContext state = TermContext.build(context, term, true);
            termStats[i] = searcher.termStatistics(term, state);
            this.termContexts.put(term, state);
            ++i;
        }
        String field = query.getField();
        if (field != null) {
            this.stats = this.similarity.computeWeight(query.getBoost(), searcher.collectionStatistics(query.getField()), termStats);
        }
    }

    @Override
    public Query getQuery() {
        return this.query;
    }

    @Override
    public float getValueForNormalization() throws IOException {
        return this.stats == null ? 1.0f : this.stats.getValueForNormalization();
    }

    @Override
    public void normalize(float queryNorm, float topLevelBoost) {
        if (this.stats != null) {
            this.stats.normalize(queryNorm, topLevelBoost);
        }
    }

    @Override
    public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
        if (this.stats == null) {
            return null;
        }
        return new SpanScorer(this.query.getSpans(context, acceptDocs, this.termContexts), this, this.similarity.simScorer(this.stats, context));
    }

    @Override
    public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
        int newDoc;
        SpanScorer scorer = (SpanScorer)this.scorer(context, true, false, context.reader().getLiveDocs());
        if (scorer != null && (newDoc = scorer.advance(doc)) == doc) {
            float freq = scorer.sloppyFreq();
            Similarity.SimScorer docScorer = this.similarity.simScorer(this.stats, context);
            ComplexExplanation result = new ComplexExplanation();
            result.setDescription("weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:");
            Explanation scoreExplanation = docScorer.explain(doc, new Explanation(freq, "phraseFreq=" + freq));
            result.addDetail(scoreExplanation);
            result.setValue(scoreExplanation.getValue());
            result.setMatch(true);
            return result;
        }
        return new ComplexExplanation(false, 0.0f, "no matching term");
    }
}

