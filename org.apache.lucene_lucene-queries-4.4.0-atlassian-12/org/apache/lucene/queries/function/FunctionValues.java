/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.Explanation
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueFloat
 */
package org.apache.lucene.queries.function;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueFloat;

public abstract class FunctionValues {
    public byte byteVal(int doc) {
        throw new UnsupportedOperationException();
    }

    public short shortVal(int doc) {
        throw new UnsupportedOperationException();
    }

    public float floatVal(int doc) {
        throw new UnsupportedOperationException();
    }

    public int intVal(int doc) {
        throw new UnsupportedOperationException();
    }

    public long longVal(int doc) {
        throw new UnsupportedOperationException();
    }

    public double doubleVal(int doc) {
        throw new UnsupportedOperationException();
    }

    public String strVal(int doc) {
        throw new UnsupportedOperationException();
    }

    public boolean boolVal(int doc) {
        return this.intVal(doc) != 0;
    }

    public boolean bytesVal(int doc, BytesRef target) {
        String s = this.strVal(doc);
        if (s == null) {
            target.length = 0;
            return false;
        }
        target.copyChars((CharSequence)s);
        return true;
    }

    public Object objectVal(int doc) {
        return Float.valueOf(this.floatVal(doc));
    }

    public boolean exists(int doc) {
        return true;
    }

    public int ordVal(int doc) {
        throw new UnsupportedOperationException();
    }

    public int numOrd() {
        throw new UnsupportedOperationException();
    }

    public abstract String toString(int var1);

    public ValueFiller getValueFiller() {
        return new ValueFiller(){
            private final MutableValueFloat mval = new MutableValueFloat();

            @Override
            public MutableValue getValue() {
                return this.mval;
            }

            @Override
            public void fillValue(int doc) {
                this.mval.value = FunctionValues.this.floatVal(doc);
            }
        };
    }

    public void byteVal(int doc, byte[] vals) {
        throw new UnsupportedOperationException();
    }

    public void shortVal(int doc, short[] vals) {
        throw new UnsupportedOperationException();
    }

    public void floatVal(int doc, float[] vals) {
        throw new UnsupportedOperationException();
    }

    public void intVal(int doc, int[] vals) {
        throw new UnsupportedOperationException();
    }

    public void longVal(int doc, long[] vals) {
        throw new UnsupportedOperationException();
    }

    public void doubleVal(int doc, double[] vals) {
        throw new UnsupportedOperationException();
    }

    public void strVal(int doc, String[] vals) {
        throw new UnsupportedOperationException();
    }

    public Explanation explain(int doc) {
        return new Explanation(this.floatVal(doc), this.toString(doc));
    }

    public ValueSourceScorer getScorer(IndexReader reader) {
        return new ValueSourceScorer(reader, this);
    }

    public ValueSourceScorer getRangeScorer(IndexReader reader, String lowerVal, String upperVal, boolean includeLower, boolean includeUpper) {
        float lower = lowerVal == null ? Float.NEGATIVE_INFINITY : Float.parseFloat(lowerVal);
        float upper = upperVal == null ? Float.POSITIVE_INFINITY : Float.parseFloat(upperVal);
        final float l = lower;
        final float u = upper;
        if (includeLower && includeUpper) {
            return new ValueSourceScorer(reader, this){

                @Override
                public boolean matchesValue(int doc) {
                    float docVal = FunctionValues.this.floatVal(doc);
                    return docVal >= l && docVal <= u;
                }
            };
        }
        if (includeLower && !includeUpper) {
            return new ValueSourceScorer(reader, this){

                @Override
                public boolean matchesValue(int doc) {
                    float docVal = FunctionValues.this.floatVal(doc);
                    return docVal >= l && docVal < u;
                }
            };
        }
        if (!includeLower && includeUpper) {
            return new ValueSourceScorer(reader, this){

                @Override
                public boolean matchesValue(int doc) {
                    float docVal = FunctionValues.this.floatVal(doc);
                    return docVal > l && docVal <= u;
                }
            };
        }
        return new ValueSourceScorer(reader, this){

            @Override
            public boolean matchesValue(int doc) {
                float docVal = FunctionValues.this.floatVal(doc);
                return docVal > l && docVal < u;
            }
        };
    }

    public static abstract class ValueFiller {
        public abstract MutableValue getValue();

        public abstract void fillValue(int var1);
    }
}

