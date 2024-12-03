/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.payloads;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.search.payloads.AveragePayloadFunction;
import com.atlassian.lucene36.search.payloads.PayloadFunction;
import com.atlassian.lucene36.search.spans.NearSpansOrdered;
import com.atlassian.lucene36.search.spans.NearSpansUnordered;
import com.atlassian.lucene36.search.spans.SpanNearQuery;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.SpanScorer;
import com.atlassian.lucene36.search.spans.SpanWeight;
import com.atlassian.lucene36.search.spans.Spans;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class PayloadNearQuery
extends SpanNearQuery {
    protected String fieldName;
    protected PayloadFunction function;

    public PayloadNearQuery(SpanQuery[] clauses, int slop, boolean inOrder) {
        this(clauses, slop, inOrder, new AveragePayloadFunction());
    }

    public PayloadNearQuery(SpanQuery[] clauses, int slop, boolean inOrder, PayloadFunction function) {
        super(clauses, slop, inOrder);
        this.fieldName = clauses[0].getField();
        this.function = function;
    }

    public Weight createWeight(Searcher searcher) throws IOException {
        return new PayloadNearSpanWeight(this, searcher);
    }

    public Object clone() {
        int sz = this.clauses.size();
        SpanQuery[] newClauses = new SpanQuery[sz];
        for (int i = 0; i < sz; ++i) {
            newClauses[i] = (SpanQuery)((SpanQuery)this.clauses.get(i)).clone();
        }
        PayloadNearQuery boostingNearQuery = new PayloadNearQuery(newClauses, this.slop, this.inOrder, this.function);
        boostingNearQuery.setBoost(this.getBoost());
        return boostingNearQuery;
    }

    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("payloadNear([");
        Iterator i = this.clauses.iterator();
        while (i.hasNext()) {
            SpanQuery clause = (SpanQuery)i.next();
            buffer.append(clause.toString(field));
            if (!i.hasNext()) continue;
            buffer.append(", ");
        }
        buffer.append("], ");
        buffer.append(this.slop);
        buffer.append(", ");
        buffer.append(this.inOrder);
        buffer.append(")");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.fieldName == null ? 0 : this.fieldName.hashCode());
        result = 31 * result + (this.function == null ? 0 : this.function.hashCode());
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
        PayloadNearQuery other = (PayloadNearQuery)obj;
        if (this.fieldName == null ? other.fieldName != null : !this.fieldName.equals(other.fieldName)) {
            return false;
        }
        return !(this.function == null ? other.function != null : !this.function.equals(other.function));
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public class PayloadNearSpanScorer
    extends SpanScorer {
        Spans spans;
        protected float payloadScore;
        private int payloadsSeen;
        Similarity similarity;

        protected PayloadNearSpanScorer(Spans spans, Weight weight, Similarity similarity, byte[] norms) throws IOException {
            super(spans, weight, similarity, norms);
            this.similarity = this.getSimilarity();
            this.spans = spans;
        }

        public void getPayloads(Spans[] subSpans) throws IOException {
            for (int i = 0; i < subSpans.length; ++i) {
                if (subSpans[i] instanceof NearSpansOrdered) {
                    if (((NearSpansOrdered)subSpans[i]).isPayloadAvailable()) {
                        this.processPayloads(((NearSpansOrdered)subSpans[i]).getPayload(), subSpans[i].start(), subSpans[i].end());
                    }
                    this.getPayloads(((NearSpansOrdered)subSpans[i]).getSubSpans());
                    continue;
                }
                if (!(subSpans[i] instanceof NearSpansUnordered)) continue;
                if (((NearSpansUnordered)subSpans[i]).isPayloadAvailable()) {
                    this.processPayloads(((NearSpansUnordered)subSpans[i]).getPayload(), subSpans[i].start(), subSpans[i].end());
                }
                this.getPayloads(((NearSpansUnordered)subSpans[i]).getSubSpans());
            }
        }

        protected void processPayloads(Collection<byte[]> payLoads, int start, int end) {
            for (byte[] thePayload : payLoads) {
                this.payloadScore = PayloadNearQuery.this.function.currentScore(this.doc, PayloadNearQuery.this.fieldName, start, end, this.payloadsSeen, this.payloadScore, this.similarity.scorePayload(this.doc, PayloadNearQuery.this.fieldName, this.spans.start(), this.spans.end(), thePayload, 0, thePayload.length));
                ++this.payloadsSeen;
            }
        }

        @Override
        protected boolean setFreqCurrentDoc() throws IOException {
            if (!this.more) {
                return false;
            }
            this.doc = this.spans.doc();
            this.freq = 0.0f;
            this.payloadScore = 0.0f;
            this.payloadsSeen = 0;
            do {
                int matchLength = this.spans.end() - this.spans.start();
                this.freq += this.getSimilarity().sloppyFreq(matchLength);
                Spans[] spansArr = new Spans[]{this.spans};
                this.getPayloads(spansArr);
                this.more = this.spans.next();
            } while (this.more && this.doc == this.spans.doc());
            return true;
        }

        @Override
        public float score() throws IOException {
            return super.score() * PayloadNearQuery.this.function.docScore(this.doc, PayloadNearQuery.this.fieldName, this.payloadsSeen, this.payloadScore);
        }

        @Override
        protected Explanation explain(int doc) throws IOException {
            Explanation result = new Explanation();
            Explanation nonPayloadExpl = super.explain(doc);
            result.addDetail(nonPayloadExpl);
            Explanation payloadExpl = PayloadNearQuery.this.function.explain(doc, this.payloadsSeen, this.payloadScore);
            result.addDetail(payloadExpl);
            result.setValue(nonPayloadExpl.getValue() * payloadExpl.getValue());
            result.setDescription("PayloadNearQuery, product of:");
            return result;
        }
    }

    public class PayloadNearSpanWeight
    extends SpanWeight {
        public PayloadNearSpanWeight(SpanQuery query, Searcher searcher) throws IOException {
            super(query, searcher);
        }

        public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
            return new PayloadNearSpanScorer(this.query.getSpans(reader), this, this.similarity, reader.norms(this.query.getField()));
        }
    }
}

