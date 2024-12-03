/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.math3.distribution.TDistribution
 */
package org.apache.poi.ss.formula.functions;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Fixed3ArgFunction;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public final class TDist
extends Fixed3ArgFunction
implements FreeRefFunction {
    public static final TDist instance = new TDist();

    static double tdistOneTail(double x, int degreesOfFreedom) {
        TDistribution tdist = new TDistribution(null, (double)degreesOfFreedom);
        return 1.0 - tdist.cumulativeProbability(x);
    }

    static double tdistTwoTails(double x, int degreesOfFreedom) {
        return 2.0 * TDist.tdistOneTail(x, degreesOfFreedom);
    }

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg1, ValueEval arg2, ValueEval arg3) {
        try {
            Double number1 = TDist.evaluateValue(arg1, srcRowIndex, srcColumnIndex);
            if (number1 == null) {
                return ErrorEval.VALUE_INVALID;
            }
            if (number1 < 0.0) {
                return ErrorEval.NUM_ERROR;
            }
            Double number2 = TDist.evaluateValue(arg2, srcRowIndex, srcColumnIndex);
            if (number2 == null) {
                return ErrorEval.VALUE_INVALID;
            }
            int degreesOfFreedom = number2.intValue();
            if (degreesOfFreedom < 1) {
                return ErrorEval.NUM_ERROR;
            }
            Double number3 = TDist.evaluateValue(arg3, srcRowIndex, srcColumnIndex);
            if (number3 == null) {
                return ErrorEval.VALUE_INVALID;
            }
            int tails = number3.intValue();
            if (tails != 1 && tails != 2) {
                return ErrorEval.NUM_ERROR;
            }
            if (tails == 2) {
                return new NumberEval(TDist.tdistTwoTails(number1, degreesOfFreedom));
            }
            return new NumberEval(TDist.tdistOneTail(number1, degreesOfFreedom));
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length == 3) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1], args[2]);
        }
        return ErrorEval.VALUE_INVALID;
    }

    private static Double evaluateValue(ValueEval arg, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        ValueEval veText = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        String strText1 = OperandResolver.coerceValueToString(veText);
        return OperandResolver.parseDouble(strText1);
    }
}

