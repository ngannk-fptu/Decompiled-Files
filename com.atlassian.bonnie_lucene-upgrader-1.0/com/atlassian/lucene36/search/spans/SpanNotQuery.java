/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.Spans;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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
    public Object clone() {
        SpanNotQuery spanNotQuery = new SpanNotQuery((SpanQuery)this.include.clone(), (SpanQuery)this.exclude.clone());
        spanNotQuery.setBoost(this.getBoost());
        return spanNotQuery;
    }

    @Override
    public Spans getSpans(final IndexReader reader) throws IOException {
        return new Spans(){
            private Spans includeSpans;
            private boolean moreInclude;
            private Spans excludeSpans;
            private boolean moreExclude;
            {
                this.includeSpans = SpanNotQuery.this.include.getSpans(reader);
                this.moreInclude = true;
                this.excludeSpans = SpanNotQuery.this.exclude.getSpans(reader);
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
            public boolean isPayloadAvailable() {
                return this.includeSpans.isPayloadAvailable();
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
            clone = (SpanNotQuery)this.clone();
            clone.include = rewrittenInclude;
        }
        if ((rewrittenExclude = (SpanQuery)this.exclude.rewrite(reader)) != this.exclude) {
            if (clone == null) {
                clone = (SpanNotQuery)this.clone();
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

