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
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public final class DollarDe
extends Fixed2ArgFunction
implements FreeRefFunction {
    public static final FreeRefFunction instance = new DollarDe();

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg1, ValueEval arg2) {
        try {
            double valueFractional;
            Double number1 = DollarDe.evaluateValue(arg1, srcRowIndex, srcColumnIndex);
            if (number1 == null) {
                return ErrorEval.VALUE_INVALID;
            }
            Double number2 = DollarDe.evaluateValue(arg2, srcRowIndex, srcColumnIndex);
            if (number2 == null) {
                return ErrorEval.VALUE_INVALID;
            }
            int fraction = number2.intValue();
            if (fraction < 0) {
                return ErrorEval.NUM_ERROR;
            }
            if (fraction == 0) {
                return ErrorEval.DIV_ZERO;
            }
            int fractionLength = String.valueOf(fraction).length();
            boolean negative = false;
            long valueLong = number1.longValue();
            if (valueLong < 0L) {
                negative = true;
                valueLong = -valueLong;
                number1 = -number1.doubleValue();
            }
            if ((valueFractional = number1 - (double)valueLong) == 0.0) {
                return new NumberEval(valueLong);
            }
            BigDecimal inflated = BigDecimal.valueOf(valueFractional).multiply(BigDecimal.valueOf(Math.pow(10.0, fractionLength)));
            BigDecimal calc = inflated.divide(BigDecimal.valueOf(fraction), MathContext.DECIMAL128);
            BigDecimal result = calc.add(BigDecimal.valueOf(valueLong));
            if (negative) {
                result = result.multiply(BigDecimal.valueOf(-1L));
            }
            return new NumberEval(result.doubleValue());
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length == 2) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
        }
        return ErrorEval.VALUE_INVALID;
    }

    private static Double evaluateValue(ValueEval arg, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        ValueEval veText = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        String strText1 = OperandResolver.coerceValueToString(veText);
        return OperandResolver.parseDouble(strText1);
    }
}

