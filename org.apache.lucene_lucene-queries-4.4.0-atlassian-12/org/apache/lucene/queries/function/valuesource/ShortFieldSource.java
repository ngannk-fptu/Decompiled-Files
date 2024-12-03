/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.FieldCache$ShortParser
 *  org.apache.lucene.search.FieldCache$Shorts
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;
import org.apache.lucene.search.FieldCache;

@Deprecated
public class ShortFieldSource
extends FieldCacheSource {
    final FieldCache.ShortParser parser;

    public ShortFieldSource(String field) {
        this(field, null);
    }

    public ShortFieldSource(String field, FieldCache.ShortParser parser) {
        super(field);
        this.parser = parser;
    }

    @Override
    public String description() {
        return "short(" + this.field + ')';
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FieldCache.Shorts arr = this.cache.getShorts(readerContext.reader(), this.field, this.parser, false);
        return new FunctionValues(){

            @Override
            public byte byteVal(int doc) {
                return (byte)arr.get(doc);
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
                return Short.toString(arr.get(doc));
            }

            @Override
            public String toString(int doc) {
                return ShortFieldSource.this.description() + '=' + this.shortVal(doc);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != ShortFieldSource.class) {
            return false;
        }
        ShortFieldSource other = (ShortFieldSource)o;
        return super.equals(other) && (this.parser == null ? other.parser == null : this.parser.getClass() == other.parser.getClass());
    }

    @Override
    public int hashCode() {
        int h = this.parser == null ? Short.class.hashCode() : this.parser.getClass().hashCode();
        return h += super.hashCode();
    }
}

