/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueDouble
 */
package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueDouble;

public abstract class DoubleDocValues
extends FunctionValues {
    protected final ValueSource vs;

    public DoubleDocValues(ValueSource vs) {
        this.vs = vs;
    }

    @Override
    public byte byteVal(int doc) {
        return (byte)this.doubleVal(doc);
    }

    @Override
    public short shortVal(int doc) {
        return (short)this.doubleVal(doc);
    }

    @Override
    public float floatVal(int doc) {
        return (float)this.doubleVal(doc);
    }

    @Override
    public int intVal(int doc) {
        return (int)this.doubleVal(doc);
    }

    @Override
    public long longVal(int doc) {
        return (long)this.doubleVal(doc);
    }

    @Override
    public boolean boolVal(int doc) {
        return this.doubleVal(doc) != 0.0;
    }

    @Override
    public abstract double doubleVal(int var1);

    @Override
    public String strVal(int doc) {
        return Double.toString(this.doubleVal(doc));
    }

    @Override
    public Object objectVal(int doc) {
        return this.exists(doc) ? Double.valueOf(this.doubleVal(doc)) : null;
    }

    @Override
    public String toString(int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
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
                this.mval.value = DoubleDocValues.this.doubleVal(doc);
                this.mval.exists = DoubleDocValues.this.exists(doc);
            }
        };
    }
}

