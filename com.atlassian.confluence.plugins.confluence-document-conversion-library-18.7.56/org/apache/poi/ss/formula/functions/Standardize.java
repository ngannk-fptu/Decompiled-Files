/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.math.BigDecimal;
import java.math.MathContext;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Fixed3ArgFunction;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public final class Standardize
extends Fixed3ArgFunction
implements FreeRefFunction {
    public static final Standardize instance = new Standardize();

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg1, ValueEval arg2, ValueEval arg3) {
        try {
            Double xval = Standardize.evaluateValue(arg1, srcRowIndex, srcColumnIndex);
            if (xval == null) {
                return ErrorEval.VALUE_INVALID;
            }
            Double mean = Standardize.evaluateValue(arg2, srcRowIndex, srcColumnIndex);
            if (mean == null) {
                return ErrorEval.VALUE_INVALID;
            }
            Double stdev = Standardize.evaluateValue(arg3, srcRowIndex, srcColumnIndex);
            if (stdev == null) {
                return ErrorEval.VALUE_INVALID;
            }
            if (stdev <= 0.0) {
                return ErrorEval.NUM_ERROR;
            }
            BigDecimal result = BigDecimal.valueOf(xval - mean).divide(BigDecimal.valueOf(stdev), MathContext.DECIMAL128);
            return new NumberEval(result.doubleValue());
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

