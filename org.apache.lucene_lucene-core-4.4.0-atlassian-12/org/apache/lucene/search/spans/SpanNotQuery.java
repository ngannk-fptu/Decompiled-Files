/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.apache.lucene.util.ToStringUtils;

public class SpanNotQuery
extends SpanQuery
implements Cloneable {
    private SpanQuery include;
    private SpanQuery exclude;

    public SpanNotQuery(SpanQuery include, SpanQuery exclude) {
        this.include = include;
        this.exclude = exclude;
        if (!include.getField().equals(exclude.getField())) {
            throw new IllegalArgumentException("Clauses must have same field.");
        }
    }

    public SpanQuery getInclude() {
        return this.include;
    }

    public SpanQuery getExclude() {
        return this.exclude;
    }

    @Override
    public String getField() {
        return this.include.getField();
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        this.include.extractTerms(terms);
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("spanNot(");
        buffer.append(this.include.toString(field));
        buffer.append(", ");
        buffer.append(this.exclude.toString(field));
        buffer.append(")");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public SpanNotQuery clone() {
        SpanNotQuery spanNotQuery = new SpanNotQuery((SpanQuery)this.include.clone(), (SpanQuery)this.exclude.clone());
        spanNotQuery.setBoost(this.getBoost());
        return spanNotQuery;
    }

    @Override
    public Spans getSpans(final AtomicReaderContext context, final Bits acceptDocs, final Map<Term, TermContext> termContexts) throws IOException {
        return new Spans(){
            private Spans includeSpans;
            private boolean moreInclude;
            private Spans excludeSpans;
            private boolean moreExclude;
            {
                this.includeSpans = SpanNotQuery.this.include.getSpans(context, acceptDocs, termContexts);
                this.moreInclude = true;
                this.excludeSpans = SpanNotQuery.this.exclude.getSpans(context, acceptDocs, termContexts);
                this.moreExclude = this.excludeSpans.next();
            }

            @Override
            public boolean next() throws IOException {
                if (this.moreInclude) {
                    this.moreInclude = this.includeSpans.next();
                }
                while (this.moreInclude && this.moreExclude) {
                    if (this.includeSpans.doc() > this.excludeSpans.doc()) {
                        this.moreExclude = this.excludeSpans.skipTo(this.includeSpans.doc());
                    }
                    while (this.moreExclude && this.includeSpans.doc() == this.excludeSpans.doc() && this.excludeSpans.end() <= this.includeSpans.start()) {
                        this.moreExclude = this.excludeSpans.next();
                    }
                    if (!this.moreExclude || this.includeSpans.doc() != this.excludeSpans.doc() || this.includeSpans.end() <= this.excludeSpans.start()) break;
                    this.moreInclude = this.includeSpans.next();
                }
                return this.moreInclude;
            }

            @Override
            public boolean skipTo(int target) throws IOException {
                if (this.moreInclude) {
                    this.moreInclude = this.includeSpans.skipTo(target);
                }
                if (!this.moreInclude) {
                    return false;
                }
                if (this.moreExclude && this.includeSpans.doc() > this.excludeSpans.doc()) {
                    this.moreExclude = this.excludeSpans.skipTo(this.includeSpans.doc());
                }
                while (this.moreExclude && this.includeSpans.doc() == this.excludeSpans.doc() && this.excludeSpans.end() <= this.includeSpans.start()) {
                    this.moreExclude = this.excludeSpans.next();
                }
                if (!this.moreExclude || this.includeSpans.doc() != this.excludeSpans.doc() || this.includeSpans.end() <= this.excludeSpans.start()) {
                    return true;
                }
                return this.next();
            }

            @Override
            public int doc() {
                return this.includeSpans.doc();
            }

            @Override
            public int start() {
                return this.includeSpans.start();
            }

            @Override
            public int end() {
                return this.includeSpans.end();
            }

            @Override
            public Collection<byte[]> getPayload() throws IOException {
                ArrayList<byte[]> result = null;
                if (this.includeSpans.isPayloadAvailable()) {
                    result = new ArrayList<byte[]>(this.includeSpans.getPayload());
                }
                return result;
            }

            @Override
            public boolean isPayloadAvailable() throws IOException {
                return this.includeSpans.isPayloadAvailable();
            }

            @Override
            public long cost() {
                return this.includeSpans.cost();
            }

            public String toString() {
                return "spans(" + SpanNotQuery.this.toString() + ")";
            }
        };
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        SpanQuery rewrittenExclude;
        SpanNotQuery clone = null;
        SpanQuery rewrittenInclude = (SpanQuery)this.include.rewrite(reader);
        if (rewrittenInclude != this.include) {
            clone = this.clone();
            clone.include = rewrittenInclude;
        }
        if ((rewrittenExclude = (SpanQuery)this.exclude.rewrite(reader)) != this.exclude) {
            if (clone == null) {
                clone = this.clone();
            }
            clone.exclude = rewrittenExclude;
        }
        if (clone != null) {
            return clone;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpanNotQuery)) {
            return false;
        }
        SpanNotQuery other = (SpanNotQuery)o;
        return this.include.equals(other.include) && this.exclude.equals(other.exclude) && this.getBoost() == other.getBoost();
    }

    @Override
    public int hashCode() {
        int h = this.include.hashCode();
        h = h << 1 | h >>> 31;
        h ^= this.exclude.hashCode();
        h = h << 1 | h >>> 31;
        return h ^= Float.floatToRawIntBits(this.getBoost());
    }
}

