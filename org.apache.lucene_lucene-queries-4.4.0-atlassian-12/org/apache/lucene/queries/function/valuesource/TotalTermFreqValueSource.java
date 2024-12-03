/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.LongDocValues;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;

public class TotalTermFreqValueSource
extends ValueSource {
    protected final String field;
    protected final String indexedField;
    protected final String val;
    protected final BytesRef indexedBytes;

    public TotalTermFreqValueSource(String field, String val, String indexedField, BytesRef indexedBytes) {
        this.field = field;
        this.val = val;
        this.indexedField = indexedField;
        this.indexedBytes = indexedBytes;
    }

    public String name() {
        return "totaltermfreq";
    }

    @Override
    public String description() {
        return this.name() + '(' + this.field + ',' + this.val + ')';
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        return (FunctionValues)context.get(this);
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        long totalTermFreq = 0L;
        for (AtomicReaderContext readerContext : searcher.getTopReaderContext().leaves()) {
            long val = readerContext.reader().totalTermFreq(new Term(this.indexedField, this.indexedBytes));
            if (val == -1L) {
                totalTermFreq = -1L;
                break;
            }
            totalTermFreq += val;
        }
        final long ttf = totalTermFreq;
        context.put(this, new LongDocValues(this){

            @Override
            public long longVal(int doc) {
                return ttf;
            }
        });
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.indexedField.hashCode() * 29 + this.indexedBytes.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        TotalTermFreqValueSource other = (TotalTermFreqValueSource)o;
        return this.indexedField.equals(other.indexedField) && this.indexedBytes.equals((Object)other.indexedBytes);
    }
}

