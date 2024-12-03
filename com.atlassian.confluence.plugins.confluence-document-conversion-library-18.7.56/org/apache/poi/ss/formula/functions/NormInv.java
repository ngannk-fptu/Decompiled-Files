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
import org.apache.poi.ss.formula.functions.Fixed3ArgFunction;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public final class NormInv
extends Fixed3ArgFunction
implements FreeRefFunction {
    public static final NormInv instance = new NormInv();

    static double inverse(double probability, double mean, double stdev) {
        NormalDistribution normalDistribution = new NormalDistribution(mean, stdev);
        return normalDistribution.inverseCumulativeProbability(probability);
    }

    private NormInv() {
    }

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg1, ValueEval arg2, ValueEval arg3) {
        try {
            Double probability = NormInv.evaluateValue(arg1, srcRowIndex, srcColumnIndex);
            if (probability == null) {
                return ErrorEval.VALUE_INVALID;
            }
            if (probability <= 0.0 || probability >= 1.0) {
                return ErrorEval.NUM_ERROR;
            }
            Double mean = NormInv.evaluateValue(arg2, srcRowIndex, srcColumnIndex);
            if (mean == null) {
                return ErrorEval.VALUE_INVALID;
            }
            Double stdev = NormInv.evaluateValue(arg3, srcRowIndex, srcColumnIndex);
            if (stdev == null) {
                return ErrorEval.VALUE_INVALID;
            }
            if (stdev <= 0.0) {
                return ErrorEval.NUM_ERROR;
            }
            return new NumberEval(NormInv.inverse(probability, mean, stdev));
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

