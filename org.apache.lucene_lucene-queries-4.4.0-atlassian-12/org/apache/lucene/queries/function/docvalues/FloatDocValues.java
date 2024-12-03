/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueFloat
 */
package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueFloat;

public abstract class FloatDocValues
extends FunctionValues {
    protected final ValueSource vs;

    public FloatDocValues(ValueSource vs) {
        this.vs = vs;
    }

    @Override
    public byte byteVal(int doc) {
        return (byte)this.floatVal(doc);
    }

    @Override
    public short shortVal(int doc) {
        return (short)this.floatVal(doc);
    }

    @Override
    public abstract float floatVal(int var1);

    @Override
    public int intVal(int doc) {
        return (int)this.floatVal(doc);
    }

    @Override
    public long longVal(int doc) {
        return (long)this.floatVal(doc);
    }

    @Override
    public double doubleVal(int doc) {
        return this.floatVal(doc);
    }

    @Override
    public String strVal(int doc) {
        return Float.toString(this.floatVal(doc));
    }

    @Override
    public Object objectVal(int doc) {
        return this.exists(doc) ? Float.valueOf(this.floatVal(doc)) : null;
    }

    @Override
    public String toString(int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
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
                this.mval.value = FloatDocValues.this.floatVal(doc);
                this.mval.exists = FloatDocValues.this.exists(doc);
            }
        };
    }
}

