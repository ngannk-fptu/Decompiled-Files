/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RelationalOperationEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public final class Switch
implements FreeRefFunction {
    public static final FreeRefFunction instance = new Switch();

    private Switch() {
    }

    @Override
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        ValueEval expression;
        if (args.length < 3) {
            return ErrorEval.NA;
        }
        try {
            expression = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
        }
        catch (Exception e) {
            return ErrorEval.NA;
        }
        for (int i = 1; i < args.length; i += 2) {
            try {
                BoolEval boolEval;
                boolean booleanValue;
                ValueEval value = OperandResolver.getSingleValue(args[i], ec.getRowIndex(), ec.getColumnIndex());
                ValueEval result = args[i + 1];
                ValueEval evaluate = RelationalOperationEval.EqualEval.evaluate(new ValueEval[]{expression, value}, ec.getRowIndex(), ec.getColumnIndex());
                if (evaluate instanceof BoolEval && (booleanValue = (boolEval = (BoolEval)evaluate).getBooleanValue())) {
                    return result;
                }
            }
            catch (EvaluationException e) {
                return ErrorEval.NA;
            }
            if (i + 2 != args.length - 1) continue;
            return args[args.length - 1];
        }
        return ErrorEval.NA;
    }
}

