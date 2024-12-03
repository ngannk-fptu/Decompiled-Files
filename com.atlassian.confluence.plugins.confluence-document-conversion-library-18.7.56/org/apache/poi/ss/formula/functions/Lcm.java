/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.math3.util.ArithmeticUtils
 */
package org.apache.poi.ss.formula.functions;

import java.util.ArrayList;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public class Lcm
implements FreeRefFunction {
    public static final Lcm instance = new Lcm();
    private static final long MAX_OUTPUT = (long)Math.pow(2.0, 53.0);

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        if (args.length == 1) {
            try {
                ValueEval v1 = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
                double d = OperandResolver.coerceValueToDouble(v1);
                if (this.isInvalidInput(d)) {
                    return ErrorEval.NUM_ERROR;
                }
                return new NumberEval((long)d);
            }
            catch (EvaluationException ee) {
                return ErrorEval.VALUE_INVALID;
            }
        }
        try {
            ArrayList<Long> evals = new ArrayList<Long>();
            for (ValueEval arg : args) {
                ValueEval ve = OperandResolver.getSingleValue(arg, ec.getRowIndex(), ec.getColumnIndex());
                double d = OperandResolver.coerceValueToDouble(ve);
                if (this.isInvalidInput(d)) {
                    return ErrorEval.NUM_ERROR;
                }
                evals.add((long)d);
            }
            long result = (Long)evals.get(0);
            for (int i = 1; i < evals.size(); ++i) {
                if ((result = ArithmeticUtils.lcm((long)result, (long)((Long)evals.get(i)))) <= MAX_OUTPUT) continue;
                return ErrorEval.NUM_ERROR;
            }
            return new NumberEval(result);
        }
        catch (EvaluationException ee) {
            return ErrorEval.VALUE_INVALID;
        }
    }

    private boolean isInvalidInput(double d) {
        return d < 0.0;
    }
}

