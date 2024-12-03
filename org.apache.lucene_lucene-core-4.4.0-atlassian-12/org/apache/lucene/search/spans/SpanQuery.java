/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.spans.SpanWeight;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;

public abstract class SpanQuery
extends Query {
    public abstract Spans getSpans(AtomicReaderContext var1, Bits var2, Map<Term, TermContext> var3) throws IOException;

    public abstract String getField();

    @Override
    public Weight createWeight(IndexSearcher searcher) throws IOException {
        return new SpanWeight(this, searcher);
    }
}

