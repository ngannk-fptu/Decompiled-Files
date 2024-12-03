/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.NearSpansOrdered;
import org.apache.lucene.search.spans.NearSpansUnordered;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

public class SpanNearQuery
extends SpanQuery
implements Cloneable {
    protected List<SpanQuery> clauses;
    protected int slop;
    protected boolean inOrder;
    protected String field;
    private boolean collectPayloads;

    public SpanNearQuery(SpanQuery[] clauses, int slop, boolean inOrder) {
        this(clauses, slop, inOrder, true);
    }

    public SpanNearQuery(SpanQuery[] clauses, int slop, boolean inOrder, boolean collectPayloads) {
        this.clauses = new ArrayList<SpanQuery>(clauses.length);
        for (int i = 0; i < clauses.length; ++i) {
            SpanQuery clause = clauses[i];
            if (i == 0) {
                this.field = clause.getField();
            } else if (!clause.getField().equals(this.field)) {
                throw new IllegalArgumentException("Clauses must have same field.");
            }
            this.clauses.add(clause);
        }
        this.collectPayloads = collectPayloads;
        this.slop = slop;
        this.inOrder = inOrder;
    }

    public SpanQuery[] getClauses() {
        return this.clauses.toArray(new SpanQuery[this.clauses.size()]);
    }

    public int getSlop() {
        return this.slop;
    }

    public boolean isInOrder() {
        return this.inOrder;
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
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("spanNear([");
        Iterator<SpanQuery> i = this.clauses.iterator();
        while (i.hasNext()) {
            SpanQuery clause = i.next();
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
    public Spans getSpans(AtomicReaderContext context, Bits acceptDocs, Map<Term, TermContext> termContexts) throws IOException {
        if (this.clauses.size() == 0) {
            return new SpanOrQuery(this.getClauses()).getSpans(context, acceptDocs, termContexts);
        }
        if (this.clauses.size() == 1) {
            return this.clauses.get(0).getSpans(context, acceptDocs, termContexts);
        }
        return this.inOrder ? new NearSpansOrdered(this, context, acceptDocs, termContexts, this.collectPayloads) : new NearSpansUnordered(this, context, acceptDocs, termContexts);
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        SpanNearQuery clone = null;
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
    public SpanNearQuery clone() {
        int sz = this.clauses.size();
        SpanQuery[] newClauses = new SpanQuery[sz];
        for (int i = 0; i < sz; ++i) {
            newClauses[i] = (SpanQuery)this.clauses.get(i).clone();
        }
        SpanNearQuery spanNearQuery = new SpanNearQuery(newClauses, this.slop, this.inOrder);
        spanNearQuery.setBoost(this.getBoost());
        return spanNearQuery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpanNearQuery)) {
            return false;
        }
        SpanNearQuery spanNearQuery = (SpanNearQuery)o;
        if (this.inOrder != spanNearQuery.inOrder) {
            return false;
        }
        if (this.slop != spanNearQuery.slop) {
            return false;
        }
        if (!this.clauses.equals(spanNearQuery.clauses)) {
            return false;
        }
        return this.getBoost() == spanNearQuery.getBoost();
    }

    @Override
    public int hashCode() {
        int result = this.clauses.hashCode();
        result ^= result << 14 | result >>> 19;
        result += Float.floatToRawIntBits(this.getBoost());
        result += this.slop;
        return result ^= this.inOrder ? -1716530243 : 0;
    }
}

