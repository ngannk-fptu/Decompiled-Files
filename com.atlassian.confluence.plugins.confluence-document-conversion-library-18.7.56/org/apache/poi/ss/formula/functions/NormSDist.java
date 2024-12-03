/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Fixed1ArgFunction;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.NormDist;

public final class NormSDist
extends Fixed1ArgFunction
implements FreeRefFunction {
    public static final NormSDist instance = new NormSDist();

    private NormSDist() {
    }

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg1) {
        try {
            Double xval = NormSDist.evaluateValue(arg1, srcRowIndex, srcColumnIndex);
            if (xval == null) {
                return ErrorEval.VALUE_INVALID;
            }
            if (xval < 0.0) {
                return ErrorEval.NUM_ERROR;
            }
            return new NumberEval(NormDist.probability(xval, 0.0, 1.0, true));
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length == 1) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0]);
        }
        return ErrorEval.VALUE_INVALID;
    }

    private static Double evaluateValue(ValueEval arg, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        ValueEval veText = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        String strText1 = OperandResolver.coerceValueToString(veText);
        return OperandResolver.parseDouble(strText1);
    }
}

