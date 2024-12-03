/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.Spans;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FieldMaskingSpanQuery
extends SpanQuery {
    private SpanQuery maskedQuery;
    private String field;

    public FieldMaskingSpanQuery(SpanQuery maskedQuery, String maskedField) {
        this.maskedQuery = maskedQuery;
        this.field = maskedField;
    }

    @Override
    public String getField() {
        return this.field;
    }

    public SpanQuery getMaskedQuery() {
        return this.maskedQuery;
    }

    @Override
    public Spans getSpans(IndexReader reader) throws IOException {
        return this.maskedQuery.getSpans(reader);
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        this.maskedQuery.extractTerms(terms);
    }

    @Override
    public Weight createWeight(Searcher searcher) throws IOException {
        return this.maskedQuery.createWeight(searcher);
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        FieldMaskingSpanQuery clone = null;
        SpanQuery rewritten = (SpanQuery)this.maskedQuery.rewrite(reader);
        if (rewritten != this.maskedQuery) {
            clone = (FieldMaskingSpanQuery)this.clone();
            clone.maskedQuery = rewritten;
        }
        if (clone != null) {
            return clone;
        }
        return this;
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("mask(");
        buffer.append(this.maskedQuery.toString(field));
        buffer.append(")");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        buffer.append(" as ");
        buffer.append(this.field);
        return buffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FieldMaskingSpanQuery)) {
            return false;
        }
        FieldMaskingSpanQuery other = (FieldMaskingSpanQuery)o;
        return this.getField().equals(other.getField()) && this.getBoost() == other.getBoost() && this.getMaskedQuery().equals(other.getMaskedQuery());
    }

    @Override
    public int hashCode() {
        return this.getMaskedQuery().hashCode() ^ this.getField().hashCode() ^ Float.floatToRawIntBits(this.getBoost());
    }
}

