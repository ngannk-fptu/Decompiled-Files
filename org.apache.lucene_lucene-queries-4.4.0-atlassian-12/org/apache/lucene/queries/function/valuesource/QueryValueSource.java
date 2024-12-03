/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.Weight
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.QueryDocValues;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;

public class QueryValueSource
extends ValueSource {
    final Query q;
    final float defVal;

    public QueryValueSource(Query q, float defVal) {
        this.q = q;
        this.defVal = defVal;
    }

    public Query getQuery() {
        return this.q;
    }

    public float getDefaultValue() {
        return this.defVal;
    }

    @Override
    public String description() {
        return "query(" + this.q + ",def=" + this.defVal + ")";
    }

    @Override
    public FunctionValues getValues(Map fcontext, AtomicReaderContext readerContext) throws IOException {
        return new QueryDocValues(this, readerContext, fcontext);
    }

    @Override
    public int hashCode() {
        return this.q.hashCode() * 29;
    }

    @Override
    public boolean equals(Object o) {
        if (QueryValueSource.class != o.getClass()) {
            return false;
        }
        QueryValueSource other = (QueryValueSource)o;
        return this.q.equals((Object)other.q) && this.defVal == other.defVal;
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        Weight w = searcher.createNormalizedWeight(this.q);
        context.put(this, w);
    }
}

