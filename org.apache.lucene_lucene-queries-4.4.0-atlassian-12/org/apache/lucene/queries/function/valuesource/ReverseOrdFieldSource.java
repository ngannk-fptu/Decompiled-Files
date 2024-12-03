/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.CompositeReader
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexReaderContext
 *  org.apache.lucene.index.ReaderUtil
 *  org.apache.lucene.index.SlowCompositeReaderWrapper
 *  org.apache.lucene.index.SortedDocValues
 *  org.apache.lucene.search.FieldCache
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.CompositeReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.lucene.search.FieldCache;

public class ReverseOrdFieldSource
extends ValueSource {
    public final String field;
    private static final int hcode = ReverseOrdFieldSource.class.hashCode();

    public ReverseOrdFieldSource(String field) {
        this.field = field;
    }

    @Override
    public String description() {
        return "rord(" + this.field + ')';
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        IndexReader topReader = ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext).reader();
        SlowCompositeReaderWrapper r = topReader instanceof CompositeReader ? new SlowCompositeReaderWrapper((CompositeReader)topReader) : (AtomicReader)topReader;
        final int off = readerContext.docBase;
        final SortedDocValues sindex = FieldCache.DEFAULT.getTermsIndex((AtomicReader)r, this.field);
        final int end = sindex.getValueCount();
        return new IntDocValues(this){

            @Override
            public int intVal(int doc) {
                return end - sindex.getOrd(doc + off) - 1;
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != ReverseOrdFieldSource.class) {
            return false;
        }
        ReverseOrdFieldSource other = (ReverseOrdFieldSource)o;
        return this.field.equals(other.field);
    }

    @Override
    public int hashCode() {
        return hcode + this.field.hashCode();
    }
}

