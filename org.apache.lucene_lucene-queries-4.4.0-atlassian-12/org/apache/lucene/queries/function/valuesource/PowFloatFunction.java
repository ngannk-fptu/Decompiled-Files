/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.DualFloatFunction;

public class PowFloatFunction
extends DualFloatFunction {
    public PowFloatFunction(ValueSource a, ValueSource b) {
        super(a, b);
    }

    @Override
    protected String name() {
        return "pow";
    }

    @Override
    protected float func(int doc, FunctionValues aVals, FunctionValues bVals) {
        return (float)Math.pow(aVals.floatVal(doc), bVals.floatVal(doc));
    }
}

