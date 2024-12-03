/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.FieldCache$IntParser
 *  org.apache.lucene.search.FieldCache$Ints
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueInt
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueInt;

public class IntFieldSource
extends FieldCacheSource {
    final FieldCache.IntParser parser;

    public IntFieldSource(String field) {
        this(field, null);
    }

    public IntFieldSource(String field, FieldCache.IntParser parser) {
        super(field);
        this.parser = parser;
    }

    @Override
    public String description() {
        return "int(" + this.field + ')';
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FieldCache.Ints arr = this.cache.getInts(readerContext.reader(), this.field, this.parser, true);
        final Bits valid = this.cache.getDocsWithField(readerContext.reader(), this.field);
        return new IntDocValues(this){
            final MutableValueInt val;
            {
                super(vs);
                this.val = new MutableValueInt();
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
                return Integer.toString(arr.get(doc));
            }

            @Override
            public Object objectVal(int doc) {
                return valid.get(doc) ? Integer.valueOf(arr.get(doc)) : null;
            }

            @Override
            public boolean exists(int doc) {
                return valid.get(doc);
            }

            @Override
            public String toString(int doc) {
                return IntFieldSource.this.description() + '=' + this.intVal(doc);
            }

            @Override
            public ValueSourceScorer getRangeScorer(IndexReader reader, String lowerVal, String upperVal, boolean includeLower, boolean includeUpper) {
                int upper;
                int lower;
                if (lowerVal == null) {
                    lower = Integer.MIN_VALUE;
                } else {
                    lower = Integer.parseInt(lowerVal);
                    if (!includeLower && lower < Integer.MAX_VALUE) {
                        ++lower;
                    }
                }
                if (upperVal == null) {
                    upper = Integer.MAX_VALUE;
                } else {
                    upper = Integer.parseInt(upperVal);
                    if (!includeUpper && upper > Integer.MIN_VALUE) {
                        --upper;
                    }
                }
                final int ll = lower;
                final int uu = upper;
                return new ValueSourceScorer(reader, this){

                    @Override
                    public boolean matchesValue(int doc) {
                        int val = arr.get(doc);
                        return val >= ll && val <= uu;
                    }
                };
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
                        this.mval.value = arr.get(doc);
                        this.mval.exists = valid.get(doc);
                    }
                };
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != IntFieldSource.class) {
            return false;
        }
        IntFieldSource other = (IntFieldSource)o;
        return super.equals(other) && (this.parser == null ? other.parser == null : this.parser.getClass() == other.parser.getClass());
    }

    @Override
    public int hashCode() {
        int h = this.parser == null ? Integer.class.hashCode() : this.parser.getClass().hashCode();
        return h += super.hashCode();
    }
}

