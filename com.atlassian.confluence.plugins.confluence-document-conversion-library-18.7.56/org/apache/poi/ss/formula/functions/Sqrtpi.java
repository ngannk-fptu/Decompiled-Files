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
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.util.NumberToTextConverter;

public class Sqrtpi
implements FreeRefFunction {
    public static final Sqrtpi instance = new Sqrtpi();

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0]);
    }

    private ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        try {
            ValueEval v1 = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            double d = OperandResolver.coerceValueToDouble(v1);
            if (this.isInvalidInput(d)) {
                return ErrorEval.NUM_ERROR;
            }
            double result = Math.sqrt(Math.PI * d);
            return new NumberEval(Double.parseDouble(NumberToTextConverter.toText(result)));
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private boolean isInvalidInput(double d) {
        return d < 0.0;
    }
}

