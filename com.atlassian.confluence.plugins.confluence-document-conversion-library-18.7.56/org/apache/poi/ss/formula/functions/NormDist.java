/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.math3.distribution.NormalDistribution
 */
package org.apache.poi.ss.formula.functions;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Fixed4ArgFunction;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public final class NormDist
extends Fixed4ArgFunction
implements FreeRefFunction {
    public static final NormDist instance = new NormDist();

    static double probability(double x, double mean, double stdev, boolean cumulative) {
        NormalDistribution normalDistribution = new NormalDistribution(mean, stdev);
        return cumulative ? normalDistribution.cumulativeProbability(x) : normalDistribution.density(x);
    }

    private NormDist() {
    }

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg1, ValueEval arg2, ValueEval arg3, ValueEval arg4) {
        try {
            Double xval = NormDist.evaluateValue(arg1, srcRowIndex, srcColumnIndex);
            if (xval == null) {
                return ErrorEval.VALUE_INVALID;
            }
            Double mean = NormDist.evaluateValue(arg2, srcRowIndex, srcColumnIndex);
            if (mean == null) {
                return ErrorEval.VALUE_INVALID;
            }
            Double stdev = NormDist.evaluateValue(arg3, srcRowIndex, srcColumnIndex);
            if (stdev == null) {
                return ErrorEval.VALUE_INVALID;
            }
            if (stdev <= 0.0) {
                return ErrorEval.NUM_ERROR;
            }
            Boolean cumulative = OperandResolver.coerceValueToBoolean(arg4, false);
            if (cumulative == null) {
                return ErrorEval.VALUE_INVALID;
            }
            return new NumberEval(NormDist.probability(xval, mean, stdev, cumulative));
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length == 4) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1], args[2], args[3]);
        }
        return ErrorEval.VALUE_INVALID;
    }

    private static Double evaluateValue(ValueEval arg, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        ValueEval veText = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        String strText1 = OperandResolver.coerceValueToString(veText);
        return OperandResolver.parseDouble(strText1);
    }
}

