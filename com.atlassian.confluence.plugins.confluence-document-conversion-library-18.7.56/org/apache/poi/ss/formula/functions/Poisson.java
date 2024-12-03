/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.math3.distribution.PoissonDistribution
 */
package org.apache.poi.ss.formula.functions;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.NumericFunction;

public class Poisson
implements FreeRefFunction {
    public static final Poisson instance = new Poisson();
    private static final double DEFAULT_RETURN_RESULT = 1.0;

    private static boolean isDefaultResult(double x, double mean) {
        return x == 0.0 && mean == 0.0;
    }

    private static void checkArgument(double aDouble) throws EvaluationException {
        NumericFunction.checkValue(aDouble);
        if (aDouble < 0.0) {
            throw new EvaluationException(ErrorEval.NUM_ERROR);
        }
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        return Poisson.evaluate(args, ec.getRowIndex(), ec.getColumnIndex());
    }

    public static ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length != 3) {
            return ErrorEval.VALUE_INVALID;
        }
        ValueEval arg0 = args[0];
        ValueEval arg1 = args[1];
        ValueEval arg2 = args[2];
        try {
            double x;
            try {
                x = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            }
            catch (EvaluationException ee) {
                return ErrorEval.VALUE_INVALID;
            }
            double mean = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            if (Poisson.isDefaultResult(x, mean)) {
                return new NumberEval(1.0);
            }
            Poisson.checkArgument(x);
            Poisson.checkArgument(mean);
            boolean cumulative = ((BoolEval)arg2).getBooleanValue();
            PoissonDistribution poissonDistribution = new PoissonDistribution(mean);
            double result = cumulative ? poissonDistribution.cumulativeProbability((int)x) : poissonDistribution.probability((int)x);
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}

