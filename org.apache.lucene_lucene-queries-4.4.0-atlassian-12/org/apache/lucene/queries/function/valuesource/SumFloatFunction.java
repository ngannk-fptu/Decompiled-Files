/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.MultiFloatFunction;

public class SumFloatFunction
extends MultiFloatFunction {
    public SumFloatFunction(ValueSource[] sources) {
        super(sources);
    }

    @Override
    protected String name() {
        return "sum";
    }

    @Override
    protected float func(int doc, FunctionValues[] valsArr) {
        float val = 0.0f;
        for (FunctionValues vals : valsArr) {
            val += vals.floatVal(doc);
        }
        return val;
    }
}

