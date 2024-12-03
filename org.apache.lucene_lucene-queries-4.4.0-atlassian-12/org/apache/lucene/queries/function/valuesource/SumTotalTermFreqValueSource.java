/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.search.IndexSearcher
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.LongDocValues;
import org.apache.lucene.search.IndexSearcher;

public class SumTotalTermFreqValueSource
extends ValueSource {
    protected final String indexedField;

    public SumTotalTermFreqValueSource(String indexedField) {
        this.indexedField = indexedField;
    }

    public String name() {
        return "sumtotaltermfreq";
    }

    @Override
    public String description() {
        return this.name() + '(' + this.indexedField + ')';
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        return (FunctionValues)context.get(this);
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        long sumTotalTermFreq = 0L;
        for (AtomicReaderContext readerContext : searcher.getTopReaderContext().leaves()) {
            Terms terms;
            Fields fields = readerContext.reader().fields();
            if (fields == null || (terms = fields.terms(this.indexedField)) == null) continue;
            long v = terms.getSumTotalTermFreq();
            if (v == -1L) {
                sumTotalTermFreq = -1L;
                break;
            }
            sumTotalTermFreq += v;
        }
        final long ttf = sumTotalTermFreq;
        context.put(this, new LongDocValues(this){

            @Override
            public long longVal(int doc) {
                return ttf;
            }
        });
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.indexedField.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        SumTotalTermFreqValueSource other = (SumTotalTermFreqValueSource)o;
        return this.indexedField.equals(other.indexedField);
    }
}

