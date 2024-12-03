/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.search.spans.SpanWeight;
import com.atlassian.lucene36.search.spans.Spans;
import java.io.IOException;

public abstract class SpanQuery
extends Query {
    public abstract Spans getSpans(IndexReader var1) throws IOException;

    public abstract String getField();

    public Weight createWeight(Searcher searcher) throws IOException {
        return new SpanWeight(this, searcher);
    }
}

