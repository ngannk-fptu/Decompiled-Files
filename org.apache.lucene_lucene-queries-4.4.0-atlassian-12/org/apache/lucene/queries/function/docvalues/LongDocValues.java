/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueLong
 */
package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueLong;

public abstract class LongDocValues
extends FunctionValues {
    protected final ValueSource vs;

    public LongDocValues(ValueSource vs) {
        this.vs = vs;
    }

    @Override
    public byte byteVal(int doc) {
        return (byte)this.longVal(doc);
    }

    @Override
    public short shortVal(int doc) {
        return (short)this.longVal(doc);
    }

    @Override
    public float floatVal(int doc) {
        return this.longVal(doc);
    }

    @Override
    public int intVal(int doc) {
        return (int)this.longVal(doc);
    }

    @Override
    public abstract long longVal(int var1);

    @Override
    public double doubleVal(int doc) {
        return this.longVal(doc);
    }

    @Override
    public boolean boolVal(int doc) {
        return this.longVal(doc) != 0L;
    }

    @Override
    public String strVal(int doc) {
        return Long.toString(this.longVal(doc));
    }

    @Override
    public Object objectVal(int doc) {
        return this.exists(doc) ? Long.valueOf(this.longVal(doc)) : null;
    }

    @Override
    public String toString(int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
    }

    @Override
    public FunctionValues.ValueFiller getValueFiller() {
        return new FunctionValues.ValueFiller(){
            private final MutableValueLong mval = new MutableValueLong();

            @Override
            public MutableValue getValue() {
                return this.mval;
            }

            @Override
            public void fillValue(int doc) {
                this.mval.value = LongDocValues.this.longVal(doc);
                this.mval.exists = LongDocValues.this.exists(doc);
            }
        };
    }
}

