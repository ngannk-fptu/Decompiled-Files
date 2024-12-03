/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.AggregateFunction;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.NumericFunction;

public final class Irr
implements Function {
    private static final int MAX_ITERATION_COUNT = 1000;
    private static final double ABSOLUTE_ACCURACY = 1.0E-7;
    private static final Logger LOGGER = LogManager.getLogger(Irr.class);

    @Override
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length == 0 || args.length > 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            double[] values = AggregateFunction.ValueCollector.collectValues(args[0]);
            double guess = args.length == 2 ? NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex) : 0.1;
            double result = Irr.irr(values, guess);
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    public static double irr(double[] income) {
        return Irr.irr(income, 0.1);
    }

    public static double irr(double[] values, double guess) {
        double x0 = guess;
        for (int i = 0; i < 1000; ++i) {
            double factor = 1.0 + x0;
            double denominator = factor;
            if (denominator == 0.0) {
                LOGGER.atWarn().log("Returning NaN because IRR has found an denominator of 0");
                return Double.NaN;
            }
            double fValue = values[0];
            double fDerivative = 0.0;
            for (int k = 1; k < values.length; ++k) {
                double value = values[k];
                fValue += value / denominator;
                fDerivative -= (double)k * value / (denominator *= factor);
            }
            if (fDerivative == 0.0) {
                LOGGER.atWarn().log("Returning NaN because IRR has found an fDerivative of 0");
                return Double.NaN;
            }
            double x1 = x0 - fValue / fDerivative;
            if (Math.abs(x1 - x0) <= 1.0E-7) {
                return x1;
            }
            x0 = x1;
        }
        LOGGER.atWarn().log("Returning NaN because IRR has reached max number of iterations allowed: {}", (Object)1000);
        return Double.NaN;
    }
}

