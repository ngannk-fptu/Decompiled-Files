/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.NumericDocValues
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.similarities.TFIDFSimilarity
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.valuesource.ConstDoubleDocValues;
import org.apache.lucene.queries.function.valuesource.IDFValueSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.TFIDFSimilarity;

public class NormValueSource
extends ValueSource {
    protected final String field;

    public NormValueSource(String field) {
        this.field = field;
    }

    public String name() {
        return "norm";
    }

    @Override
    public String description() {
        return this.name() + '(' + this.field + ')';
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        context.put("searcher", searcher);
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        IndexSearcher searcher = (IndexSearcher)context.get("searcher");
        final TFIDFSimilarity similarity = IDFValueSource.asTFIDF(searcher.getSimilarity(), this.field);
        if (similarity == null) {
            throw new UnsupportedOperationException("requires a TFIDFSimilarity (such as DefaultSimilarity)");
        }
        final NumericDocValues norms = readerContext.reader().getNormValues(this.field);
        if (norms == null) {
            return new ConstDoubleDocValues(0.0, this);
        }
        return new FloatDocValues(this){

            @Override
            public float floatVal(int doc) {
                return similarity.decodeNormValue((long)((byte)norms.get(doc)));
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        return this.field.equals(((NormValueSource)o).field);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.field.hashCode();
    }
}

