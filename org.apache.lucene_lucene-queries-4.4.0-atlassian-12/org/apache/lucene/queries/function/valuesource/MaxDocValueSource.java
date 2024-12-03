/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.IndexSearcher
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.ConstIntDocValues;
import org.apache.lucene.search.IndexSearcher;

public class MaxDocValueSource
extends ValueSource {
    public String name() {
        return "maxdoc";
    }

    @Override
    public String description() {
        return this.name() + "()";
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        context.put("searcher", searcher);
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        IndexSearcher searcher = (IndexSearcher)context.get("searcher");
        return new ConstIntDocValues(searcher.getIndexReader().maxDoc(), this);
    }

    @Override
    public boolean equals(Object o) {
        return this.getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}

