/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.FieldCache$ByteParser
 *  org.apache.lucene.search.FieldCache$Bytes
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;
import org.apache.lucene.search.FieldCache;

@Deprecated
public class ByteFieldSource
extends FieldCacheSource {
    private final FieldCache.ByteParser parser;

    public ByteFieldSource(String field) {
        this(field, null);
    }

    public ByteFieldSource(String field, FieldCache.ByteParser parser) {
        super(field);
        this.parser = parser;
    }

    @Override
    public String description() {
        return "byte(" + this.field + ')';
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FieldCache.Bytes arr = this.cache.getBytes(readerContext.reader(), this.field, this.parser, false);
        return new FunctionValues(){

            @Override
            public byte byteVal(int doc) {
                return arr.get(doc);
            }

            @Override
            public short shortVal(int doc) {
                return arr.get(doc);
            }

            @Override
            public float floatVal(int doc) {
                return arr.get(doc);
            }

            @Override
            public int intVal(int doc) {
                return arr.get(doc);
            }

            @Override
            public long longVal(int doc) {
                return arr.get(doc);
            }

            @Override
            public double doubleVal(int doc) {
                return arr.get(doc);
            }

            @Override
            public String strVal(int doc) {
                return Byte.toString(arr.get(doc));
            }

            @Override
            public String toString(int doc) {
                return ByteFieldSource.this.description() + '=' + this.byteVal(doc);
            }

            @Override
            public Object objectVal(int doc) {
                return arr.get(doc);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != ByteFieldSource.class) {
            return false;
        }
        ByteFieldSource other = (ByteFieldSource)o;
        return super.equals(other) && (this.parser == null ? other.parser == null : this.parser.getClass() == other.parser.getClass());
    }

    @Override
    public int hashCode() {
        int h = this.parser == null ? Byte.class.hashCode() : this.parser.getClass().hashCode();
        return h += super.hashCode();
    }
}

