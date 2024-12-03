/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.FieldCache$DoubleParser
 *  org.apache.lucene.search.FieldCache$Doubles
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueDouble
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueDouble;

public class DoubleFieldSource
extends FieldCacheSource {
    protected final FieldCache.DoubleParser parser;

    public DoubleFieldSource(String field) {
        this(field, null);
    }

    public DoubleFieldSource(String field, FieldCache.DoubleParser parser) {
        super(field);
        this.parser = parser;
    }

    @Override
    public String description() {
        return "double(" + this.field + ')';
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        final FieldCache.Doubles arr = this.cache.getDoubles(readerContext.reader(), this.field, this.parser, true);
        final Bits valid = this.cache.getDocsWithField(readerContext.reader(), this.field);
        return new DoubleDocValues(this){

            @Override
            public double doubleVal(int doc) {
                return arr.get(doc);
            }

            @Override
            public boolean exists(int doc) {
                return valid.get(doc);
            }

            @Override
            public ValueSourceScorer getRangeScorer(IndexReader reader, String lowerVal, String upperVal, boolean includeLower, boolean includeUpper) {
                double lower = lowerVal == null ? Double.NEGATIVE_INFINITY : Double.parseDouble(lowerVal);
                double upper = upperVal == null ? Double.POSITIVE_INFINITY : Double.parseDouble(upperVal);
                final double l = lower;
                final double u = upper;
                if (includeLower && includeUpper) {
                    return new ValueSourceScorer(reader, this){

                        @Override
                        public boolean matchesValue(int doc) {
                            double docVal = this.doubleVal(doc);
                            return docVal >= l && docVal <= u;
                        }
                    };
                }
                if (includeLower && !includeUpper) {
                    return new ValueSourceScorer(reader, this){

                        @Override
                        public boolean matchesValue(int doc) {
                            double docVal = this.doubleVal(doc);
                            return docVal >= l && docVal < u;
                        }
                    };
                }
                if (!includeLower && includeUpper) {
                    return new ValueSourceScorer(reader, this){

                        @Override
                        public boolean matchesValue(int doc) {
                            double docVal = this.doubleVal(doc);
                            return docVal > l && docVal <= u;
                        }
                    };
                }
                return new ValueSourceScorer(reader, this){

                    @Override
                    public boolean matchesValue(int doc) {
                        double docVal = this.doubleVal(doc);
                        return docVal > l && docVal < u;
                    }
                };
            }

            @Override
            public FunctionValues.ValueFiller getValueFiller() {
                return new FunctionValues.ValueFiller(){
                    private final MutableValueDouble mval = new MutableValueDouble();

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
        if (o.getClass() != DoubleFieldSource.class) {
            return false;
        }
        DoubleFieldSource other = (DoubleFieldSource)o;
        return super.equals(other) && (this.parser == null ? other.parser == null : this.parser.getClass() == other.parser.getClass());
    }

    @Override
    public int hashCode() {
        int h = this.parser == null ? Double.class.hashCode() : this.parser.getClass().hashCode();
        return h += super.hashCode();
    }
}

