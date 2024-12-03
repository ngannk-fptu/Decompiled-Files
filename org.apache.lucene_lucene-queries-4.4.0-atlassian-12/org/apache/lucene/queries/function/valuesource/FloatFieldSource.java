/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.FieldCache$FloatParser
 *  org.apache.lucene.search.FieldCache$Floats
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueFloat
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueFloat;

public class FloatFieldSource
extends FieldCacheSource {
    protected final FieldCache.FloatParser parser;

    public FloatFieldSource(String field) {
        this(field, null);
    }

    public FloatFieldSource(String field, FieldCache.FloatParser parser) {
        super(field);
        this.parser = parser;
    }

    @Override
    public String description() {
        return "float(" + this.field + ')';
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FieldCache.Floats arr = this.cache.getFloats(readerContext.reader(), this.field, this.parser, true);
        final Bits valid = this.cache.getDocsWithField(readerContext.reader(), this.field);
        return new FloatDocValues(this){

            @Override
            public float floatVal(int doc) {
                return arr.get(doc);
            }

            @Override
            public Object objectVal(int doc) {
                return valid.get(doc) ? Float.valueOf(arr.get(doc)) : null;
            }

            @Override
            public boolean exists(int doc) {
                return valid.get(doc);
            }

            @Override
            public FunctionValues.ValueFiller getValueFiller() {
                return new FunctionValues.ValueFiller(){
                    private final MutableValueFloat mval = new MutableValueFloat();

                    @Override
                    public MutableValue getValue() {
                        return this.mval;
                    }

                    @Override
                    public void fillValue(int doc) {
                        this.mval.value = arr.get(doc);
                        this.mval.exists = valid.get(doc);
                    }
                };
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != FloatFieldSource.class) {
            return false;
        }
        FloatFieldSource other = (FloatFieldSource)o;
        return super.equals(other) && (this.parser == null ? other.parser == null : this.parser.getClass() == other.parser.getClass());
    }

    @Override
    public int hashCode() {
        int h = this.parser == null ? Float.class.hashCode() : this.parser.getClass().hashCode();
        return h += super.hashCode();
    }
}

