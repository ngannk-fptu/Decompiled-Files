/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public final class TermContext {
    public final IndexReaderContext topReaderContext;
    private final TermState[] states;
    private int docFreq;
    private long totalTermFreq;

    public TermContext(IndexReaderContext context) {
        assert (context != null && context.isTopLevel);
        this.topReaderContext = context;
        this.docFreq = 0;
        int len = context.leaves() == null ? 1 : context.leaves().size();
        this.states = new TermState[len];
    }

    public TermContext(IndexReaderContext context, TermState state, int ord, int docFreq, long totalTermFreq) {
        this(context);
        this.register(state, ord, docFreq, totalTermFreq);
    }

    public static TermContext build(IndexReaderContext context, Term term, boolean cache) throws IOException {
        assert (context != null && context.isTopLevel);
        String field = term.field();
        BytesRef bytes = term.bytes();
        TermContext perReaderTermState = new TermContext(context);
        for (AtomicReaderContext ctx : context.leaves()) {
            TermsEnum termsEnum;
            Terms terms;
            Fields fields = ctx.reader().fields();
            if (fields == null || (terms = fields.terms(field)) == null || !(termsEnum = terms.iterator(null)).seekExact(bytes, cache)) continue;
            TermState termState = termsEnum.termState();
            perReaderTermState.register(termState, ctx.ord, termsEnum.docFreq(), termsEnum.totalTermFreq());
        }
        return perReaderTermState;
    }

    public void clear() {
        this.docFreq = 0;
        Arrays.fill(this.states, null);
    }

    public void register(TermState state, int ord, int docFreq, long totalTermFreq) {
        assert (state != null) : "state must not be null";
        assert (ord >= 0 && ord < this.states.length);
        assert (this.states[ord] == null) : "state for ord: " + ord + " already registered";
        this.docFreq += docFreq;
        this.totalTermFreq = this.totalTermFreq >= 0L && totalTermFreq >= 0L ? (this.totalTermFreq += totalTermFreq) : -1L;
        this.states[ord] = state;
    }

    public TermState get(int ord) {
        assert (ord >= 0 && ord < this.states.length);
        return this.states[ord];
    }

    public int docFreq() {
        return this.docFreq;
    }

    public long totalTermFreq() {
        return this.totalTermFreq;
    }

    public void setDocFreq(int docFreq) {
        this.docFreq = docFreq;
    }
}

