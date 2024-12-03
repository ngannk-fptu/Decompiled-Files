/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import java.util.Set;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TermScorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

public class TermQuery
extends Query {
    private final Term term;
    private final int docFreq;
    private final TermContext perReaderTermState;

    public TermQuery(Term t) {
        this(t, -1);
    }

    public TermQuery(Term t, int docFreq) {
        this.term = t;
        this.docFreq = docFreq;
        this.perReaderTermState = null;
    }

    public TermQuery(Term t, TermContext states) {
        assert (states != null);
        this.term = t;
        this.docFreq = states.docFreq();
        this.perReaderTermState = states;
    }

    public Term getTerm() {
        return this.term;
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
        IndexReaderContext context = searcher.getTopReaderContext();
        TermContext termState = this.perReaderTermState == null || this.perReaderTermState.topReaderContext != context ? TermContext.build(context, this.term, true) : this.perReaderTermState;
        if (this.docFreq != -1) {
            termState.setDocFreq(this.docFreq);
        }
        return new TermWeight(searcher, termState);
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        terms.add(this.getTerm());
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TermQuery)) {
            return false;
        }
        TermQuery other = (TermQuery)o;
        return this.getBoost() == other.getBoost() && this.term.equals(other.term);
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.getBoost()) ^ this.term.hashCode();
    }

    final class TermWeight
    extends Weight {
        private final Similarity similarity;
        private final Similarity.SimWeight stats;
        private final TermContext termStates;

        public TermWeight(IndexSearcher searcher, TermContext termStates) throws IOException {
            assert (termStates != null) : "TermContext must not be null";
            this.termStates = termStates;
            this.similarity = searcher.getSimilarity();
            this.stats = this.similarity.computeWeight(TermQuery.this.getBoost(), searcher.collectionStatistics(TermQuery.this.term.field()), searcher.termStatistics(TermQuery.this.term, termStates));
        }

        public String toString() {
            return "weight(" + TermQuery.this + ")";
        }

        @Override
        public Query getQuery() {
            return TermQuery.this;
        }

        @Override
        public float getValueForNormalization() {
            return this.stats.getValueForNormalization();
        }

        @Override
        public void normalize(float queryNorm, float topLevelBoost) {
            this.stats.normalize(queryNorm, topLevelBoost);
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
            assert (this.termStates.topReaderContext == ReaderUtil.getTopLevelContext(context)) : "The top-reader used to create Weight (" + this.termStates.topReaderContext + ") is not the same as the current reader's top-reader (" + ReaderUtil.getTopLevelContext(context);
            TermsEnum termsEnum = this.getTermsEnum(context);
            if (termsEnum == null) {
                return null;
            }
            DocsEnum docs = termsEnum.docs(acceptDocs, null);
            assert (docs != null);
            return new TermScorer(this, docs, this.similarity.simScorer(this.stats, context));
        }

        private TermsEnum getTermsEnum(AtomicReaderContext context) throws IOException {
            TermState state = this.termStates.get(context.ord);
            if (state == null) {
                assert (this.termNotInReader(context.reader(), TermQuery.this.term)) : "no termstate found but term exists in reader term=" + TermQuery.access$000(TermQuery.this);
                return null;
            }
            TermsEnum termsEnum = context.reader().terms(TermQuery.this.term.field()).iterator(null);
            termsEnum.seekExact(TermQuery.this.term.bytes(), state);
            return termsEnum;
        }

        private boolean termNotInReader(AtomicReader reader, Term term) throws IOException {
            return reader.docFreq(term) == 0;
        }

        @Override
        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            int newDoc;
            Scorer scorer = this.scorer(context, true, false, context.reader().getLiveDocs());
            if (scorer != null && (newDoc = scorer.advance(doc)) == doc) {
                float freq = scorer.freq();
                Similarity.SimScorer docScorer = this.similarity.simScorer(this.stats, context);
                ComplexExplanation result = new ComplexExplanation();
                result.setDescription("weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:");
                Explanation scoreExplanation = docScorer.explain(doc, new Explanation(freq, "termFreq=" + freq));
                result.addDetail(scoreExplanation);
                result.setValue(scoreExplanation.getValue());
                result.setMatch(true);
                return result;
            }
            return new ComplexExplanation(false, 0.0f, "no matching term");
        }
    }
}

