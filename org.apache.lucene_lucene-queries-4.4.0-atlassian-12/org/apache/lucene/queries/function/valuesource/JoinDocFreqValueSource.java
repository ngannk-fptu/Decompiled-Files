/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.BinaryDocValues
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexReaderContext
 *  org.apache.lucene.index.MultiFields
 *  org.apache.lucene.index.ReaderUtil
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;
import org.apache.lucene.util.BytesRef;

public class JoinDocFreqValueSource
extends FieldCacheSource {
    public static final String NAME = "joindf";
    protected final String qfield;

    public JoinDocFreqValueSource(String field, String qfield) {
        super(field);
        this.qfield = qfield;
    }

    @Override
    public String description() {
        return "joindf(" + this.field + ":(" + this.qfield + "))";
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final BinaryDocValues terms = this.cache.getTerms(readerContext.reader(), this.field, 0.5f);
        IndexReader top = ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext).reader();
        Terms t = MultiFields.getTerms((IndexReader)top, (String)this.qfield);
        final TermsEnum termsEnum = t == null ? TermsEnum.EMPTY : t.iterator(null);
        return new IntDocValues(this){
            final BytesRef ref;
            {
                super(vs);
                this.ref = new BytesRef();
            }

            @Override
            public int intVal(int doc) {
                try {
                    terms.get(doc, this.ref);
                    if (termsEnum.seekExact(this.ref, true)) {
                        return termsEnum.docFreq();
                    }
                    return 0;
                }
                catch (IOException e) {
                    throw new RuntimeException("caught exception in function " + JoinDocFreqValueSource.this.description() + " : doc=" + doc, e);
                }
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != JoinDocFreqValueSource.class) {
            return false;
        }
        JoinDocFreqValueSource other = (JoinDocFreqValueSource)o;
        if (!this.qfield.equals(other.qfield)) {
            return false;
        }
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return this.qfield.hashCode() + super.hashCode();
    }
}

