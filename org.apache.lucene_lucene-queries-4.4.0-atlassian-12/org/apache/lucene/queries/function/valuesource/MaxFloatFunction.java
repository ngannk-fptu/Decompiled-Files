/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.MultiFloatFunction;

public class MaxFloatFunction
extends MultiFloatFunction {
    public MaxFloatFunction(ValueSource[] sources) {
        super(sources);
    }

    @Override
    protected String name() {
        return "max";
    }

    @Override
    protected float func(int doc, FunctionValues[] valsArr) {
        if (valsArr.length == 0) {
            return 0.0f;
        }
        float val = Float.NEGATIVE_INFINITY;
        for (FunctionValues vals : valsArr) {
            val = Math.max(vals.floatVal(doc), val);
        }
        return val;
    }
}

