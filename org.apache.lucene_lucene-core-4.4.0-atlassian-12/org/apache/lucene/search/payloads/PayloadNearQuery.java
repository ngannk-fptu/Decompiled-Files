/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.payloads;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.ComplexExplanation;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.PayloadFunction;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.spans.NearSpansOrdered;
import org.apache.lucene.search.spans.NearSpansUnordered;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanScorer;
import org.apache.lucene.search.spans.SpanWeight;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.ToStringUtils;

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

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
        return new PayloadNearSpanWeight(this, searcher);
    }

    @Override
    public PayloadNearQuery clone() {
        int sz = this.clauses.size();
        SpanQuery[] newClauses = new SpanQuery[sz];
        for (int i = 0; i < sz; ++i) {
            newClauses[i] = (SpanQuery)((SpanQuery)this.clauses.get(i)).clone();
        }
        PayloadNearQuery boostingNearQuery = new PayloadNearQuery(newClauses, this.slop, this.inOrder, this.function);
        boostingNearQuery.setBoost(this.getBoost());
        return boostingNearQuery;
    }

    @Override
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

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.fieldName == null ? 0 : this.fieldName.hashCode());
        result = 31 * result + (this.function == null ? 0 : this.function.hashCode());
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
        PayloadNearQuery other = (PayloadNearQuery)obj;
        if (this.fieldName == null ? other.fieldName != null : !this.fieldName.equals(other.fieldName)) {
            return false;
        }
        return !(this.function == null ? other.function != null : !this.function.equals(other.function));
    }

    public class PayloadNearSpanScorer
    extends SpanScorer {
        Spans spans;
        protected float payloadScore;
        private int payloadsSeen;
        BytesRef scratch;

        protected PayloadNearSpanScorer(Spans spans, Weight weight, Similarity similarity, Similarity.SimScorer docScorer) throws IOException {
            super(spans, weight, docScorer);
            this.scratch = new BytesRef();
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
                this.scratch.bytes = thePayload;
                this.scratch.offset = 0;
                this.scratch.length = thePayload.length;
                this.payloadScore = PayloadNearQuery.this.function.currentScore(this.doc, PayloadNearQuery.this.fieldName, start, end, this.payloadsSeen, this.payloadScore, this.docScorer.computePayloadFactor(this.doc, this.spans.start(), this.spans.end(), this.scratch));
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
                this.freq += this.docScorer.computeSlopFactor(matchLength);
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
    }

    public class PayloadNearSpanWeight
    extends SpanWeight {
        public PayloadNearSpanWeight(SpanQuery query, IndexSearcher searcher) throws IOException {
            super(query, searcher);
        }

        @Override
        public Scorer scorer(AtomicReaderContext context, boolean scoreDocsInOrder, boolean topScorer, Bits acceptDocs) throws IOException {
            return new PayloadNearSpanScorer(this.query.getSpans(context, acceptDocs, this.termContexts), this, this.similarity, this.similarity.simScorer(this.stats, context));
        }

        @Override
        public Explanation explain(AtomicReaderContext context, int doc) throws IOException {
            int newDoc;
            PayloadNearSpanScorer scorer = (PayloadNearSpanScorer)this.scorer(context, true, false, context.reader().getLiveDocs());
            if (scorer != null && (newDoc = scorer.advance(doc)) == doc) {
                float freq = scorer.freq();
                Similarity.SimScorer docScorer = this.similarity.simScorer(this.stats, context);
                Explanation expl = new Explanation();
                expl.setDescription("weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:");
                Explanation scoreExplanation = docScorer.explain(doc, new Explanation(freq, "phraseFreq=" + freq));
                expl.addDetail(scoreExplanation);
                expl.setValue(scoreExplanation.getValue());
                String field = ((SpanQuery)this.getQuery()).getField();
                Explanation payloadExpl = PayloadNearQuery.this.function.explain(doc, field, scorer.payloadsSeen, scorer.payloadScore);
                ComplexExplanation result = new ComplexExplanation();
                result.addDetail(expl);
                result.addDetail(payloadExpl);
                result.setValue(expl.getValue() * payloadExpl.getValue());
                result.setDescription("PayloadNearQuery, product of:");
                return result;
            }
            return new ComplexExplanation(false, 0.0f, "no matching term");
        }
    }
}

