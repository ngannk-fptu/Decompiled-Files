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

public final class FloorMath
implements FreeRefFunction {
    public static final FloorMath instance = new FloorMath();

    private FloorMath() {
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length == 0) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            RoundingMode mode;
            Double xval = FloorMath.evaluateValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
            if (xval == null) {
                return ErrorEval.VALUE_INVALID;
            }
            double multiplier = 1.0;
            if (args.length > 1) {
                Double arg1Val = FloorMath.evaluateValue(args[1], ec.getRowIndex(), ec.getColumnIndex());
                multiplier = arg1Val != null ? arg1Val : 1.0;
            }
            boolean roundNegativeNumsDown = false;
            if (args.length > 2) {
                Double arg2Val = FloorMath.evaluateValue(args[2], ec.getRowIndex(), ec.getColumnIndex());
                boolean bl = roundNegativeNumsDown = arg2Val != null && arg2Val < 0.0;
            }
            if (roundNegativeNumsDown && xval < 0.0) {
                if (multiplier != 1.0) {
                    mode = multiplier < 0.0 ? RoundingMode.FLOOR : RoundingMode.CEILING;
                    return new NumberEval(MathX.scaledRoundUsingBigDecimal(xval, multiplier, mode));
                }
                return new NumberEval(Math.ceil(xval));
            }
            if (multiplier != 1.0) {
                mode = multiplier < 0.0 ? RoundingMode.CEILING : RoundingMode.FLOOR;
                return new NumberEval(MathX.scaledRoundUsingBigDecimal(xval, multiplier, mode));
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

