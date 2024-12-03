/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueInt
 */
package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueInt;

public abstract class IntDocValues
extends FunctionValues {
    protected final ValueSource vs;

    public IntDocValues(ValueSource vs) {
        this.vs = vs;
    }

    @Override
    public byte byteVal(int doc) {
        return (byte)this.intVal(doc);
    }

    @Override
    public short shortVal(int doc) {
        return (short)this.intVal(doc);
    }

    @Override
    public float floatVal(int doc) {
        return this.intVal(doc);
    }

    @Override
    public abstract int intVal(int var1);

    @Override
    public long longVal(int doc) {
        return this.intVal(doc);
    }

    @Override
    public double doubleVal(int doc) {
        return this.intVal(doc);
    }

    @Override
    public String strVal(int doc) {
        return Integer.toString(this.intVal(doc));
    }

    @Override
    public Object objectVal(int doc) {
        return this.exists(doc) ? Integer.valueOf(this.intVal(doc)) : null;
    }

    @Override
    public String toString(int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
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
                this.mval.value = IntDocValues.this.intVal(doc);
                this.mval.exists = IntDocValues.this.exists(doc);
            }
        };
    }
}

