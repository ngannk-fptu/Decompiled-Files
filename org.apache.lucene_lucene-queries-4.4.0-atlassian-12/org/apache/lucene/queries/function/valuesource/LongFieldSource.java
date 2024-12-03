/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.FieldCache$LongParser
 *  org.apache.lucene.search.FieldCache$Longs
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueLong
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.queries.function.docvalues.LongDocValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueLong;

public class LongFieldSource
extends FieldCacheSource {
    protected final FieldCache.LongParser parser;

    public LongFieldSource(String field) {
        this(field, null);
    }

    public LongFieldSource(String field, FieldCache.LongParser parser) {
        super(field);
        this.parser = parser;
    }

    @Override
    public String description() {
        return "long(" + this.field + ')';
    }

    public long externalToLong(String extVal) {
        return Long.parseLong(extVal);
    }

    public Object longToObject(long val) {
        return val;
    }

    public String longToString(long val) {
        return this.longToObject(val).toString();
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FieldCache.Longs arr = this.cache.getLongs(readerContext.reader(), this.field, this.parser, true);
        final Bits valid = this.cache.getDocsWithField(readerContext.reader(), this.field);
        return new LongDocValues(this){

            @Override
            public long longVal(int doc) {
                return arr.get(doc);
            }

            @Override
            public boolean exists(int doc) {
                return valid.get(doc);
            }

            @Override
            public Object objectVal(int doc) {
                return valid.get(doc) ? LongFieldSource.this.longToObject(arr.get(doc)) : null;
            }

            @Override
            public String strVal(int doc) {
                return valid.get(doc) ? LongFieldSource.this.longToString(arr.get(doc)) : null;
            }

            @Override
            public ValueSourceScorer getRangeScorer(IndexReader reader, String lowerVal, String upperVal, boolean includeLower, boolean includeUpper) {
                long upper;
                long lower;
                if (lowerVal == null) {
                    lower = Long.MIN_VALUE;
                } else {
                    lower = LongFieldSource.this.externalToLong(lowerVal);
                    if (!includeLower && lower < Long.MAX_VALUE) {
                        ++lower;
                    }
                }
                if (upperVal == null) {
                    upper = Long.MAX_VALUE;
                } else {
                    upper = LongFieldSource.this.externalToLong(upperVal);
                    if (!includeUpper && upper > Long.MIN_VALUE) {
                        --upper;
                    }
                }
                final long ll = lower;
                final long uu = upper;
                return new ValueSourceScorer(reader, this){

                    @Override
                    public boolean matchesValue(int doc) {
                        long val = arr.get(doc);
                        return val >= ll && val <= uu;
                    }
                };
            }

            @Override
            public FunctionValues.ValueFiller getValueFiller() {
                return new FunctionValues.ValueFiller(){
                    private final MutableValueLong mval;
                    {
                        this.mval = LongFieldSource.this.newMutableValueLong();
                    }

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

    protected MutableValueLong newMutableValueLong() {
        return new MutableValueLong();
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) {
            return false;
        }
        LongFieldSource other = (LongFieldSource)o;
        return super.equals(other) && (this.parser == null ? other.parser == null : this.parser.getClass() == other.parser.getClass());
    }

    @Override
    public int hashCode() {
        int h = this.parser == null ? this.getClass().hashCode() : this.parser.getClass().hashCode();
        return h += super.hashCode();
    }
}

