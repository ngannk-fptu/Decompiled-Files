/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.Spans;
import com.atlassian.lucene36.search.spans.TermSpans;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SpanTermQuery
extends SpanQuery {
    protected Term term;

    public SpanTermQuery(Term term) {
        this.term = term;
    }

    public Term getTerm() {
        return this.term;
    }

    @Override
    public String getField() {
        return this.term.field();
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        terms.add(this.term);
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (this.term.field().equals(field)) {
            buffer.append(this.term.text());
        } else {
            buffer.append(this.term.toString());
        }
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.term == null ? 0 : this.term.hashCode());
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
        SpanTermQuery other = (SpanTermQuery)obj;
        return !(this.term == null ? other.term != null : !this.term.equals(other.term));
    }

    @Override
    public Spans getSpans(IndexReader reader) throws IOException {
        return new TermSpans(reader.termPositions(this.term), this.term);
    }
}

