/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.payloads;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.payloads.PayloadFunction;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanScorer;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.SpanWeight;
import org.apache.lucene.search.spans.TermSpans;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public class PayloadTermQuery
extends SpanTermQuery {
    protected PayloadFunction function;
    private boolean includeSpanScore;

    public PayloadTermQuery(Term term, PayloadFunction function) {
        this(term, function, true);
    }

    public PayloadTermQuery(Term term, PayloadFunction function, boolean includeSpanScore) {
        super(term);
        this.function = function;
        this.includeSpanScore = includeSpanScore;
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
        return new PayloadTermWeight(this, searcher);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.function == null ? 0 : this.function.hashCode());
        result = 31 * result + (this.includeSpanScore ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PayloadTermQuery other = (PayloadTermQuery)obj;
        if (this.function == null ? other.function != null : !this.function.equals(other.function)) {
            return false;
        }
        return this.includeSpanScore == other.includeSpanScore;
    }

    protected class PayloadTermWeight
    extends SpanWeight {
        public PayloadTermWeight(PayloadTermQuery query, IndexSearcher searcher) throws IOException {
            super(query, searcher);
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
            return new PayloadTermSpanScorer((TermSpans)this.query.getSpans(context, acceptDocs, this.termContexts), this, this.similarity.simScorer(this.stats, context));
        }

        @Override
        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            int newDoc;
            PayloadTermSpanScorer scorer = (PayloadTermSpanScorer)this.scorer(context, true, false, context.reader().getLiveDocs());
            if (scorer != null && (newDoc = scorer.advance(doc)) == doc) {
                float freq = scorer.sloppyFreq();
                Similarity.SimScorer docScorer = this.similarity.simScorer(this.stats, context);
                Explanation expl = new Explanation();
                expl.setDescription("weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:");
                Explanation scoreExplanation = docScorer.explain(doc, new Explanation(freq, "phraseFreq=" + freq));
                expl.addDetail(scoreExplanation);
                expl.setValue(scoreExplanation.getValue());
                String field = ((SpanQuery)this.getQuery()).getField();
                Explanation payloadExpl = PayloadTermQuery.this.function.explain(doc, field, scorer.payloadsSeen, scorer.payloadScore);
                payloadExpl.setValue(scorer.getPayloadScore());
                ComplexExplanation result = new ComplexExplanation();
                if (PayloadTermQuery.this.includeSpanScore) {
                    result.addDetail(expl);
                    result.addDetail(payloadExpl);
                    result.setValue(expl.getValue() * payloadExpl.getValue());
                    result.setDescription("btq, product of:");
                } else {
                    result.addDetail(payloadExpl);
                    result.setValue(payloadExpl.getValue());
                    result.setDescription("btq(includeSpanScore=false), result of:");
                }
                result.setMatch(true);
                return result;
            }
            return new ComplexExplanation(false, 0.0f, "no matching term");
        }

        protected class PayloadTermSpanScorer
        extends SpanScorer {
            protected BytesRef payload;
            protected float payloadScore;
            protected int payloadsSeen;
            private final TermSpans termSpans;

            public PayloadTermSpanScorer(TermSpans spans, Weight weight, Similarity.SimScorer docScorer) throws IOException {
                super(spans, weight, docScorer);
                this.termSpans = spans;
            }

            @Override
            protected boolean setFreqCurrentDoc() throws IOException {
                if (!this.more) {
                    return false;
                }
                this.doc = this.spans.doc();
                this.freq = 0.0f;
                this.numMatches = 0;
                this.payloadScore = 0.0f;
                this.payloadsSeen = 0;
                while (this.more && this.doc == this.spans.doc()) {
                    int matchLength = this.spans.end() - this.spans.start();
                    this.freq += this.docScorer.computeSlopFactor(matchLength);
                    ++this.numMatches;
                    this.processPayload(PayloadTermWeight.this.similarity);
                    this.more = this.spans.next();
                }
                return this.more || this.freq != 0.0f;
            }

            protected void processPayload(Similarity similarity) throws IOException {
                if (this.termSpans.isPayloadAvailable()) {
                    DocsAndPositionsEnum postings = this.termSpans.getPostings();
                    this.payload = postings.getPayload();
                    this.payloadScore = this.payload != null ? PayloadTermQuery.this.function.currentScore(this.doc, PayloadTermQuery.this.term.field(), this.spans.start(), this.spans.end(), this.payloadsSeen, this.payloadScore, this.docScorer.computePayloadFactor(this.doc, this.spans.start(), this.spans.end(), this.payload)) : PayloadTermQuery.this.function.currentScore(this.doc, PayloadTermQuery.this.term.field(), this.spans.start(), this.spans.end(), this.payloadsSeen, this.payloadScore, 1.0f);
                    ++this.payloadsSeen;
                }
            }

            @Override
            public float score() throws IOException {
                return PayloadTermQuery.this.includeSpanScore ? this.getSpanScore() * this.getPayloadScore() : this.getPayloadScore();
            }

            protected float getSpanScore() throws IOException {
                return super.score();
            }

            protected float getPayloadScore() {
                return PayloadTermQuery.this.function.docScore(this.doc, PayloadTermQuery.this.term.field(), this.payloadsSeen, this.payloadScore);
            }
        }
    }
}

