/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.PriorityQueue;
import org.apache.lucene.util.ToStringUtils;

public class SpanOrQuery
extends SpanQuery
implements Cloneable {
    private List<SpanQuery> clauses;
    private String field;

    public SpanOrQuery(SpanQuery ... clauses) {
        this.clauses = new ArrayList<SpanQuery>(clauses.length);
        for (int i = 0; i < clauses.length; ++i) {
            this.addClause(clauses[i]);
        }
    }

    public final void addClause(SpanQuery clause) {
        if (this.field == null) {
            this.field = clause.getField();
        } else if (!clause.getField().equals(this.field)) {
            throw new IllegalArgumentException("Clauses must have same field.");
        }
        this.clauses.add(clause);
    }

    public SpanQuery[] getClauses() {
        return this.clauses.toArray(new SpanQuery[this.clauses.size()]);
    }

    @Override
    public String getField() {
        return this.field;
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        for (SpanQuery clause : this.clauses) {
            clause.extractTerms(terms);
        }
    }

    @Override
    public SpanOrQuery clone() {
        int sz = this.clauses.size();
        SpanQuery[] newClauses = new SpanQuery[sz];
        for (int i = 0; i < sz; ++i) {
            newClauses[i] = (SpanQuery)this.clauses.get(i).clone();
        }
        SpanOrQuery soq = new SpanOrQuery(newClauses);
        soq.setBoost(this.getBoost());
        return soq;
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        SpanOrQuery clone = null;
        for (int i = 0; i < this.clauses.size(); ++i) {
            SpanQuery c = this.clauses.get(i);
            SpanQuery query = (SpanQuery)c.rewrite(reader);
            if (query == c) continue;
            if (clone == null) {
                clone = this.clone();
            }
            clone.clauses.set(i, query);
        }
        if (clone != null) {
            return clone;
        }
        return this;
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("spanOr([");
        Iterator<SpanQuery> i = this.clauses.iterator();
        while (i.hasNext()) {
            SpanQuery clause = i.next();
            buffer.append(clause.toString(field));
            if (!i.hasNext()) continue;
            buffer.append(", ");
        }
        buffer.append("])");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpanOrQuery that = (SpanOrQuery)o;
        if (!this.clauses.equals(that.clauses)) {
            return false;
        }
        if (!this.clauses.isEmpty() && !this.field.equals(that.field)) {
            return false;
        }
        return this.getBoost() == that.getBoost();
    }

    @Override
    public int hashCode() {
        int h = this.clauses.hashCode();
        h ^= h << 10 | h >>> 23;
        return h ^= Float.floatToRawIntBits(this.getBoost());
    }

    @Override
    public Spans getSpans(final AtomicReaderContext context, final Bits acceptDocs, final Map<Term, TermContext> termContexts) throws IOException {
        if (this.clauses.size() == 1) {
            return this.clauses.get(0).getSpans(context, acceptDocs, termContexts);
        }
        return new Spans(){
            private SpanQueue queue = null;
            private long cost;

            private boolean initSpanQueue(int target) throws IOException {
                this.queue = new SpanQueue(SpanOrQuery.this.clauses.size());
                Iterator i = SpanOrQuery.this.clauses.iterator();
                while (i.hasNext()) {
                    Spans spans = ((SpanQuery)i.next()).getSpans(context, acceptDocs, termContexts);
                    this.cost += spans.cost();
                    if ((target != -1 || !spans.next()) && (target == -1 || !spans.skipTo(target))) continue;
                    this.queue.add(spans);
                }
                return this.queue.size() != 0;
            }

            @Override
            public boolean next() throws IOException {
                if (this.queue == null) {
                    return this.initSpanQueue(-1);
                }
                if (this.queue.size() == 0) {
                    return false;
                }
                if (this.top().next()) {
                    this.queue.updateTop();
                    return true;
                }
                this.queue.pop();
                return this.queue.size() != 0;
            }

            private Spans top() {
                return (Spans)this.queue.top();
            }

            @Override
            public boolean skipTo(int target) throws IOException {
                if (this.queue == null) {
                    return this.initSpanQueue(target);
                }
                boolean skipCalled = false;
                while (this.queue.size() != 0 && this.top().doc() < target) {
                    if (this.top().skipTo(target)) {
                        this.queue.updateTop();
                    } else {
                        this.queue.pop();
                    }
                    skipCalled = true;
                }
                if (skipCalled) {
                    return this.queue.size() != 0;
                }
                return this.next();
            }

            @Override
            public int doc() {
                return this.top().doc();
            }

            @Override
            public int start() {
                return this.top().start();
            }

            @Override
            public int end() {
                return this.top().end();
            }

            @Override
            public Collection<byte[]> getPayload() throws IOException {
                ArrayList<byte[]> result = null;
                Spans theTop = this.top();
                if (theTop != null && theTop.isPayloadAvailable()) {
                    result = new ArrayList<byte[]>(theTop.getPayload());
                }
                return result;
            }

            @Override
            public boolean isPayloadAvailable() throws IOException {
                Spans top = this.top();
                return top != null && top.isPayloadAvailable();
            }

            public String toString() {
                return "spans(" + SpanOrQuery.this + ")@" + (this.queue == null ? "START" : (this.queue.size() > 0 ? this.doc() + ":" + this.start() + "-" + this.end() : "END"));
            }

            @Override
            public long cost() {
                return this.cost;
            }
        };
    }

    private class SpanQueue
    extends PriorityQueue<Spans> {
        public SpanQueue(int size) {
            super(size);
        }

        @Override
        protected final boolean lessThan(Spans spans1, Spans spans2) {
            if (spans1.doc() == spans2.doc()) {
                if (spans1.start() == spans2.start()) {
                    return spans1.end() < spans2.end();
                }
                return spans1.start() < spans2.start();
            }
            return spans1.doc() < spans2.doc();
        }
    }
}

