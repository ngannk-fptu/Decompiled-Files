/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;
import org.apache.poi.ss.formula.functions.LinearRegressionFunction;

public final class Intercept
extends Fixed2ArgFunction {
    private final LinearRegressionFunction func = new LinearRegressionFunction(LinearRegressionFunction.FUNCTION.INTERCEPT);

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        return this.func.evaluate(srcRowIndex, srcColumnIndex, arg0, arg1);
    }
}

