/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.BinaryDocValues
 *  org.apache.lucene.index.FieldInfo
 *  org.apache.lucene.index.FieldInfo$DocValuesType
 *  org.apache.lucene.search.FieldCache
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.docvalues.DocTermsIndexDocValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.BytesRef;

public class BytesRefFieldSource
extends FieldCacheSource {
    public BytesRefFieldSource(String field) {
        super(field);
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        FieldInfo fieldInfo = readerContext.reader().getFieldInfos().fieldInfo(this.field);
        if (fieldInfo != null && fieldInfo.getDocValuesType() == FieldInfo.DocValuesType.BINARY) {
            final BinaryDocValues binaryValues = FieldCache.DEFAULT.getTerms(readerContext.reader(), this.field);
            return new FunctionValues(){

                @Override
                public boolean exists(int doc) {
                    return true;
                }

                @Override
                public boolean bytesVal(int doc, BytesRef target) {
                    binaryValues.get(doc, target);
                    return target.length > 0;
                }

                @Override
                public String strVal(int doc) {
                    BytesRef bytes = new BytesRef();
                    return this.bytesVal(doc, bytes) ? bytes.utf8ToString() : null;
                }

                @Override
                public Object objectVal(int doc) {
                    return this.strVal(doc);
                }

                @Override
                public String toString(int doc) {
                    return BytesRefFieldSource.this.description() + '=' + this.strVal(doc);
                }
            };
        }
        return new DocTermsIndexDocValues(this, readerContext, this.field){

            @Override
            protected String toTerm(String readableValue) {
                return readableValue;
            }

            @Override
            public Object objectVal(int doc) {
                return this.strVal(doc);
            }

            @Override
            public String toString(int doc) {
                return BytesRefFieldSource.this.description() + '=' + this.strVal(doc);
            }
        };
    }
}

