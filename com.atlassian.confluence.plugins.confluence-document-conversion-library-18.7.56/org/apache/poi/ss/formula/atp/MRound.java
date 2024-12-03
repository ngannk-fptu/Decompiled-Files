/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.atp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.NumericFunction;

final class MRound
implements FreeRefFunction {
    public static final FreeRefFunction instance = new MRound();

    private MRound() {
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            double result;
            double number = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex()));
            double multiple = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex()));
            if (multiple == 0.0) {
                result = 0.0;
            } else {
                if (number * multiple < 0.0) {
                    throw new EvaluationException(ErrorEval.NUM_ERROR);
                }
                BigDecimal bdMultiple = BigDecimal.valueOf(multiple);
                result = bdMultiple.multiply(BigDecimal.valueOf(number).divide(bdMultiple, 0, RoundingMode.HALF_UP)).doubleValue();
            }
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}

