/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.math.RoundingMode;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.MathX;

public final class FloorPrecise
implements FreeRefFunction {
    public static final FloorPrecise instance = new FloorPrecise();

    private FloorPrecise() {
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length == 0) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            Double xval = FloorPrecise.evaluateValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
            if (xval == null) {
                return ErrorEval.VALUE_INVALID;
            }
            double multiplier = 1.0;
            if (args.length > 1) {
                Double arg1Val = FloorPrecise.evaluateValue(args[1], ec.getRowIndex(), ec.getColumnIndex());
                double d = multiplier = arg1Val != null ? Math.abs(arg1Val) : 1.0;
            }
            if (multiplier != 1.0) {
                return new NumberEval(MathX.scaledRoundUsingBigDecimal(xval, multiplier, RoundingMode.FLOOR));
            }
            return new NumberEval(Math.floor(xval));
        }
        catch (EvaluationException evaluationException) {
            return evaluationException.getErrorEval();
        }
    }

    private static Double evaluateValue(ValueEval arg, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        ValueEval veText = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        String strText1 = OperandResolver.coerceValueToString(veText);
        return OperandResolver.parseDouble(strText1);
    }
}

