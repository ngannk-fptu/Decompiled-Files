/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.NumericFunction;

public class Log {
    private static final double TEN = 10.0;
    private static final double LOG_10_TO_BASE_e = Math.log(10.0);

    public static ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length != 1 && args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            double result;
            double d0 = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
            if (args.length == 1) {
                result = Math.log(d0) / LOG_10_TO_BASE_e;
            } else {
                double d1 = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
                double logE = Math.log(d0);
                result = Double.compare(d1, Math.E) == 0 ? logE : logE / Math.log(d1);
            }
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}

