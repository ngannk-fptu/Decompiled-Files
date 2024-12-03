/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.payloads;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermPositions;
import com.atlassian.lucene36.search.ComplexExplanation;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.search.payloads.PayloadFunction;
import com.atlassian.lucene36.search.spans.SpanScorer;
import com.atlassian.lucene36.search.spans.SpanTermQuery;
import com.atlassian.lucene36.search.spans.SpanWeight;
import com.atlassian.lucene36.search.spans.TermSpans;
import java.io.IOException;

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

    public Weight createWeight(Searcher searcher) throws IOException {
        return new PayloadTermWeight(this, searcher);
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.function == null ? 0 : this.function.hashCode());
        result = 31 * result + (this.includeSpanScore ? 1231 : 1237);
        return result;
    }

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
        public PayloadTermWeight(PayloadTermQuery query, Searcher searcher) throws IOException {
            super(query, searcher);
        }

        public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
            return new PayloadTermSpanScorer((TermSpans)this.query.getSpans(reader), this, this.similarity, reader.norms(this.query.getField()));
        }

        public Explanation explain(IndexReader reader, int doc) throws IOException {
            if (PayloadTermQuery.this.includeSpanScore) {
                return super.explain(reader, doc);
            }
            PayloadTermSpanScorer scorer = (PayloadTermSpanScorer)this.scorer(reader, true, false);
            return scorer.explain(doc);
        }

        protected class PayloadTermSpanScorer
        extends SpanScorer {
            protected byte[] payload;
            protected TermPositions positions;
            protected float payloadScore;
            protected int payloadsSeen;

            public PayloadTermSpanScorer(TermSpans spans, Weight weight, Similarity similarity, byte[] norms) throws IOException {
                super(spans, weight, similarity, norms);
                this.payload = new byte[256];
                this.positions = spans.getPositions();
            }

            protected boolean setFreqCurrentDoc() throws IOException {
                if (!this.more) {
                    return false;
                }
                this.doc = this.spans.doc();
                this.freq = 0.0f;
                this.payloadScore = 0.0f;
                this.payloadsSeen = 0;
                Similarity similarity1 = this.getSimilarity();
                while (this.more && this.doc == this.spans.doc()) {
                    int matchLength = this.spans.end() - this.spans.start();
                    this.freq += similarity1.sloppyFreq(matchLength);
                    this.processPayload(similarity1);
                    this.more = this.spans.next();
                }
                return this.more || this.freq != 0.0f;
            }

            protected void processPayload(Similarity similarity) throws IOException {
                if (this.positions.isPayloadAvailable()) {
                    this.payload = this.positions.getPayload(this.payload, 0);
                    this.payloadScore = PayloadTermQuery.this.function.currentScore(this.doc, PayloadTermQuery.this.term.field(), this.spans.start(), this.spans.end(), this.payloadsSeen, this.payloadScore, similarity.scorePayload(this.doc, PayloadTermQuery.this.term.field(), this.spans.start(), this.spans.end(), this.payload, 0, this.positions.getPayloadLength()));
                    ++this.payloadsSeen;
                }
            }

            public float score() throws IOException {
                return PayloadTermQuery.this.includeSpanScore ? this.getSpanScore() * this.getPayloadScore() : this.getPayloadScore();
            }

            protected float getSpanScore() throws IOException {
                return super.score();
            }

            protected float getPayloadScore() {
                return PayloadTermQuery.this.function.docScore(this.doc, PayloadTermQuery.this.term.field(), this.payloadsSeen, this.payloadScore);
            }

            protected Explanation explain(int doc) throws IOException {
                Explanation nonPayloadExpl = super.explain(doc);
                Explanation payloadBoost = new Explanation();
                float payloadScore = this.getPayloadScore();
                payloadBoost.setValue(payloadScore);
                payloadBoost.setDescription("scorePayload(...)");
                ComplexExplanation result = new ComplexExplanation();
                if (PayloadTermQuery.this.includeSpanScore) {
                    result.addDetail(nonPayloadExpl);
                    result.addDetail(payloadBoost);
                    result.setValue(nonPayloadExpl.getValue() * payloadScore);
                    result.setDescription("btq, product of:");
                } else {
                    result.addDetail(payloadBoost);
                    result.setValue(payloadScore);
                    result.setDescription("btq(includeSpanScore=false), result of:");
                }
                result.setMatch(nonPayloadExpl.getValue() == 0.0f ? Boolean.FALSE : Boolean.TRUE);
                return result;
            }
        }
    }
}

