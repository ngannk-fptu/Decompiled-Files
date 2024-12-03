/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

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
    public Spans getSpans(AtomicReaderContext context, Bits acceptDocs, Map<Term, TermContext> termContexts) throws IOException {
        return this.maskedQuery.getSpans(context, acceptDocs, termContexts);
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        this.maskedQuery.extractTerms(terms);
    }

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
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

