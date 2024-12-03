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
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueInt
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
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueInt;

public class OrdFieldSource
extends ValueSource {
    protected final String field;
    private static final int hcode = OrdFieldSource.class.hashCode();

    public OrdFieldSource(String field) {
        this.field = field;
    }

    @Override
    public String description() {
        return "ord(" + this.field + ')';
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final int off = readerContext.docBase;
        IndexReader topReader = ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext).reader();
        SlowCompositeReaderWrapper r = topReader instanceof CompositeReader ? new SlowCompositeReaderWrapper((CompositeReader)topReader) : (AtomicReader)topReader;
        final SortedDocValues sindex = FieldCache.DEFAULT.getTermsIndex((AtomicReader)r, this.field);
        return new IntDocValues(this){

            protected String toTerm(String readableValue) {
                return readableValue;
            }

            @Override
            public int intVal(int doc) {
                return sindex.getOrd(doc + off);
            }

            @Override
            public int ordVal(int doc) {
                return sindex.getOrd(doc + off);
            }

            @Override
            public int numOrd() {
                return sindex.getValueCount();
            }

            @Override
            public boolean exists(int doc) {
                return sindex.getOrd(doc + off) != 0;
            }

            @Override
            public FunctionValues.ValueFiller getValueFiller() {
                return new FunctionValues.ValueFiller(){
                    private final MutableValueInt mval = new MutableValueInt();

                    @Override
                    public MutableValue getValue() {
                        return this.mval;
                    }

                    @Override
                    public void fillValue(int doc) {
                        this.mval.value = sindex.getOrd(doc);
                        this.mval.exists = this.mval.value != 0;
                    }
                };
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass() == OrdFieldSource.class && this.field.equals(((OrdFieldSource)o).field);
    }

    @Override
    public int hashCode() {
        return hcode + this.field.hashCode();
    }
}

