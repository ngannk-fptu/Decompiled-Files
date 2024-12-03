/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.search.spans.TermSpans;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;

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
    public Spans getSpans(AtomicReaderContext context, Bits acceptDocs, Map<Term, TermContext> termContexts) throws IOException {
        TermsEnum termsEnum;
        Terms terms;
        Fields fields;
        TermContext termContext = termContexts.get(this.term);
        TermState state = termContext == null ? ((fields = context.reader().fields()) != null ? ((terms = fields.terms(this.term.field())) != null ? ((termsEnum = terms.iterator(null)).seekExact(this.term.bytes(), true) ? termsEnum.termState() : null) : null) : null) : termContext.get(context.ord);
        if (state == null) {
            return TermSpans.EMPTY_TERM_SPANS;
        }
        TermsEnum termsEnum2 = context.reader().terms(this.term.field()).iterator(null);
        termsEnum2.seekExact(this.term.bytes(), state);
        DocsAndPositionsEnum postings = termsEnum2.docsAndPositions(acceptDocs, null, 2);
        if (postings != null) {
            return new TermSpans(postings, this.term);
        }
        throw new IllegalStateException("field \"" + this.term.field() + "\" was indexed without position data; cannot run SpanTermQuery (term=" + this.term.text() + ")");
    }
}

