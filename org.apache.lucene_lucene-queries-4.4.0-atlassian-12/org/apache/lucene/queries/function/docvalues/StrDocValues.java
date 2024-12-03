/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.mutable.MutableValue
 *  org.apache.lucene.util.mutable.MutableValueStr
 */
package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueStr;

public abstract class StrDocValues
extends FunctionValues {
    protected final ValueSource vs;

    public StrDocValues(ValueSource vs) {
        this.vs = vs;
    }

    @Override
    public abstract String strVal(int var1);

    @Override
    public Object objectVal(int doc) {
        return this.exists(doc) ? this.strVal(doc) : null;
    }

    @Override
    public boolean boolVal(int doc) {
        return this.exists(doc);
    }

    @Override
    public String toString(int doc) {
        return this.vs.description() + "='" + this.strVal(doc) + "'";
    }

    @Override
    public FunctionValues.ValueFiller getValueFiller() {
        return new FunctionValues.ValueFiller(){
            private final MutableValueStr mval = new MutableValueStr();

            @Override
            public MutableValue getValue() {
                return this.mval;
            }

            @Override
            public void fillValue(int doc) {
                this.mval.exists = StrDocValues.this.bytesVal(doc, this.mval.value);
            }
        };
    }
}

