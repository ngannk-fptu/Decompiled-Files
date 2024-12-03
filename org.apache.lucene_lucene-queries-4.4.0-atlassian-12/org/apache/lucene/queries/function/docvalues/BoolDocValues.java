/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueBool
 */
package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueBool;

public abstract class BoolDocValues
extends FunctionValues {
    protected final ValueSource vs;

    public BoolDocValues(ValueSource vs) {
        this.vs = vs;
    }

    @Override
    public abstract boolean boolVal(int var1);

    @Override
    public byte byteVal(int doc) {
        return this.boolVal(doc) ? (byte)1 : 0;
    }

    @Override
    public short shortVal(int doc) {
        return this.boolVal(doc) ? (short)1 : 0;
    }

    @Override
    public float floatVal(int doc) {
        return this.boolVal(doc) ? 1.0f : 0.0f;
    }

    @Override
    public int intVal(int doc) {
        return this.boolVal(doc) ? 1 : 0;
    }

    @Override
    public long longVal(int doc) {
        return this.boolVal(doc) ? 1L : 0L;
    }

    @Override
    public double doubleVal(int doc) {
        return this.boolVal(doc) ? 1.0 : 0.0;
    }

    @Override
    public String strVal(int doc) {
        return Boolean.toString(this.boolVal(doc));
    }

    @Override
    public Object objectVal(int doc) {
        return this.exists(doc) ? Boolean.valueOf(this.boolVal(doc)) : null;
    }

    @Override
    public String toString(int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
    }

    @Override
    public FunctionValues.ValueFiller getValueFiller() {
        return new FunctionValues.ValueFiller(){
            private final MutableValueBool mval = new MutableValueBool();

            @Override
            public MutableValue getValue() {
                return this.mval;
            }

            @Override
            public void fillValue(int doc) {
                this.mval.value = BoolDocValues.this.boolVal(doc);
                this.mval.exists = BoolDocValues.this.exists(doc);
            }
        };
    }
}

