/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.util.Arrays;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.AggregateFunction;
import org.apache.poi.ss.formula.functions.FinanceLib;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.NumericFunction;

public final class Npv
implements Function {
    @Override
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int nArgs = args.length;
        if (nArgs < 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            double rate = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
            ValueEval[] vargs = (ValueEval[])Arrays.copyOfRange(args, 1, args.length, ValueEval[].class);
            double[] values = AggregateFunction.ValueCollector.collectValues(vargs);
            double result = FinanceLib.npv(rate, values);
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}

