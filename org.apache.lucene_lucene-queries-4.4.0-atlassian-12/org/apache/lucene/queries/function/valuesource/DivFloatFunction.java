/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.DualFloatFunction;

public class DivFloatFunction
extends DualFloatFunction {
    public DivFloatFunction(ValueSource a, ValueSource b) {
        super(a, b);
    }

    @Override
    protected String name() {
        return "div";
    }

    @Override
    protected float func(int doc, FunctionValues aVals, FunctionValues bVals) {
        return aVals.floatVal(doc) / bVals.floatVal(doc);
    }
}

