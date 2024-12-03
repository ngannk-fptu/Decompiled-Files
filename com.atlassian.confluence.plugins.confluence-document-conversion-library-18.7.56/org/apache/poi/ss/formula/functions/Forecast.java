/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

import java.util.List;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.ArrayFunctionUtils;
import org.apache.poi.ss.formula.functions.DoubleList;
import org.apache.poi.ss.formula.functions.Fixed3ArgFunction;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.MathX;

public class Forecast
extends Fixed3ArgFunction
implements FreeRefFunction {
    public static final Forecast instance = new Forecast();

    private Forecast() {
    }

    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        try {
            Double x = Forecast.evaluateValue(arg0, srcRowIndex, srcColumnIndex);
            if (x == null || x.isNaN() || x.isInfinite()) {
                return ErrorEval.VALUE_INVALID;
            }
            List<DoubleList> arrays = ArrayFunctionUtils.getNumberArrays(arg1, arg2);
            double[] arrY = arrays.get(0).toArray();
            double[] arrX = arrays.get(1).toArray();
            double averageY = MathX.average(arrY);
            double averageX = MathX.average(arrX);
            double bnum = 0.0;
            double bdem = 0.0;
            int len = arrY.length;
            for (int i = 0; i < len; ++i) {
                double diff0 = arrX[i] - averageX;
                bnum += diff0 * (arrY[i] - averageY);
                bdem += Math.pow(diff0, 2.0);
            }
            if (bdem == 0.0) {
                return ErrorEval.DIV_ZERO;
            }
            double b = bnum / bdem;
            double a = averageY - b * averageX;
            double res = a + b * x;
            return new NumberEval(res);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        catch (Exception e) {
            return ErrorEval.NA;
        }
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 3) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1], args[2]);
    }

    private static Double evaluateValue(ValueEval arg, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        ValueEval veText = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        String strText1 = OperandResolver.coerceValueToString(veText);
        return OperandResolver.parseDouble(strText1);
    }
}

